//package com.xinyan.mongo.service.sharding;
//
//import com.xinyan.mongo.service.IDynamicDataBaseService;
//import com.xinyan.mongo.utils.DateUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.time.DateUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.stream.Collectors;
//
//*
// * @author weimin_ruan
// * @date 2019/9/23
//
//
//@Slf4j
//public abstract class BaseCarrierShardingHandler<T, S extends CarrierData<T>> {
//    public static final String KEY_PHONE = "phone";
//    public static final String KEY_TOKEN = "token";
//    @Autowired
//    protected CarrierShardingService carrierShardingService;
//    @Autowired
//    protected IDynamicDataBaseService dynamicDataBaseService;
//    @Value("${mongo.collection.carrier.default}")
//    protected String collection;
//    @Value("${mongo.collection.sharding.call}")
//    protected String carrierCallDatabase;
//    @Value("${mongo.collection.sharding.sms}")
//    protected String carrierSmsDatabase;
//    @Value("${mongo.collection.sharding.net}")
//    protected String carrierNetDatabase;
//    @Value("${mongo.collection.sharding.bill}")
//    protected String carrierBillDatabase;
//    @Value("${mongo.collection.sharding.basic}")
//    protected String carrierBasicDatabase;
//    @Value("${mongo.collection.sharding.recharge}")
//    protected String carrierRechargeDatabase;
//    @Value("${mongo.collection.sharding.packages}")
//    protected String carrierPackagesDatabase;
//    @Value("${mongo.collection.sharding.family}")
//    protected String carrierFamilyDatabase;
//    @Value("${emailReceiver}")
//    protected String emailReceiver;
//
//    public Map<String, Integer> save(CarrierShardingContext context) {
//        final DataMongoObject dataMongoObject = context.getDataMongoObject();
//        final UserParamsLog userParamsLog = context.getUserParamsLog();
//        final CarrierInfo carrierInfo = context.getCarrierInfo();
//        final boolean isReParse = context.isReParse();
//        final CarrierCrawlRecord hisCrawlRecord = context.getCarrierCrawlRecord();
//        final boolean isWhiteList = context.isWhiteList();
//        final String type = getType().name();
//        final String token = dataMongoObject.getToken();
//        final long start = System.currentTimeMillis();
//        final String phone = context.getPhone();
//        final String createTime = DateUtils.stringToString(userParamsLog.getCreateTime(), DateUtils.DEFAULT_TIME_FORMAT, DateUtils.PATTERN6);
//        log.info("处理运营商数据{}开始，token:{},phone:{}", type, token, ShieldUtil.shieldPhone(phone));
//        //数据汇总
//        final Map<String, Integer> hisRecord = getExistingRecord(hisCrawlRecord);
//        //获取数据明细，现在数据为空时不直接返回，需要走补全
//        List<T> details = getDataDetails(carrierInfo);
//        prepare(details, userParamsLog);
//        try {
//            List<Future<?>> futures = new ArrayList<>(10);
//            Map<String, Integer> result = new HashMap<>();
//            //根据月份分组
//            Map<String, List<T>> detailMap = details.stream().collect(Collectors.groupingBy(getGroupFunction(createTime)));
//            log.info("运营商数据{}月份，token:{},month:{}", type, token, detailMap.keySet());
//            fillMonth(detailMap);
//            log.debug("运营商数据{}月份补全，token:{},month:{}", type, token, detailMap.keySet());
//            for (Map.Entry<String, List<T>> entry : detailMap.entrySet()) {
//                final String month = entry.getKey();
//                final List<T> oneMonthDetails = entry.getValue();
//                final boolean isValidBillMonth = checkBillMonth(month, token);
//                if (!isValidBillMonth) {
//                    log.error("token:{}, 通话账单月份格式不正确", token);
//                    continue;
//                }
//                final CarrierData<T> carrierData = newCarrierDataInstance(dataMongoObject);
//                carrierData.setPhone(phone);
//                carrierData.setToken(token);
//                carrierData.setCreateTime(new Date());
//                List<T> mergeDataList = new ArrayList<>(oneMonthDetails);
//                //如果有历史记录、是白名单、不是重推解析，则合并记录
//                if (hisRecord != null && !hisRecord.isEmpty() && hisRecord.containsKey(month) && isWhiteList && !isReParse) {
//                    log.debug("运查询历史数据{}尝试合并，token:{}", type, token);
//                    S exists = queryHisData(phone, month);
//                    if (exists != null) {
//                        carrierData.setId(exists.getId());
//                        carrierData.setCreateTime(exists.getCreateTime());
//                        //执行合并
//                        mergeDataList = mergeData(oneMonthDetails, exists.getData());
//                    }
//                }
//                if (mergeDataList.isEmpty()) {
//                    log.debug("运营商数据{}为空跳过，token:{}", type, token);
//                    continue;
//                }
//                carrierData.setUpdateTime(new Date());
//                mergeDataList = mergeDataList.stream().sorted(Comparator.comparing(getSortFunction()).reversed()).collect(Collectors.toList());
//                carrierData.setData(mergeDataList);
//                //加密
//                long t1 = System.currentTimeMillis();
//                carrierCryptoUtil.encrypt(carrierData);
//                long t2 = System.currentTimeMillis();
//                log.debug("加密数据耗时,类型:{},时间:{}", getType().name(), t2 - t1);
//                final Future<?> future = save(token, carrierData, getShardingDatabaseName(), collection, month, carrierData.getPhone());
//                futures.add(future);
//                result.put(month, mergeDataList.size());
//            }
//            for (Future<?> future : futures) {
//                future.get();
//            }
//            log.debug("处理运营商数据{}结束，token:{}，耗时{} ms", type, token, System.currentTimeMillis() - start);
//            return result;
//        } catch (Exception e) {
//            String content = String.format("处理%s数据失败,token:%s", type, token);
//            log.error(content, e);
//            MailUtil.sendMail(Constant.ServerName.QZ_DATA_CENTER, content, content + "," + e.getMessage(), MailMessageType.WARN, emailReceiver);
//            return null;
//        }
//    }
//
//    private Future<?> save(String token, CarrierData<T> carrierData, String database, String collection, String month, String mobile) {
//        final String type = getType().name();
//        return threadPool.submit(() -> {
//            try {
//                //超过16M报警
//                if (ObjectUtil.isSixteenMB(carrierData)) {
//                    errorMail("数据超过16MB" + token, token);
//                    return;
//                }
//                long start = System.currentTimeMillis();
//                final String key = getKey();
//                if (KEY_PHONE.equals(key)) {
//                    dynamicDataBaseService.saveDataByPhone(carrierData, database, collection, month, mobile);
//                } else {
//                    dynamicDataBaseService.saveDataByToken(carrierData, database, collection, month, token);
//                }
//                log.info("插入{}数据完成,token:{},month:{},{}条耗时{} ms", type, token, month, carrierData.getData().size(), System.currentTimeMillis() - start);
//            } catch (Exception e) {
//                String content = String.format("保存%s数据失败,token:%s,month:%s", type, token, month);
//                log.error(content, e);
//                errorMail(content, content + "," + e.getMessage());
//            }
//        });
//    }
//
//    public List<T> query(String token, String time, String phone) {
//        long start = System.currentTimeMillis();
//        log.info("查询运营商分库数据:{}-{}-{}-{}", getType(), time, ShieldUtil.shieldPhone(phone), token);
//        Date date = DateUtil.stringToDate(time, DateUtil.fm_yyyy_MM_dd_HHmmss);
//        List<String> monthList = getMonths(token, carrierCrawlRecord, date);
//        // 任务创建当前月
//        final String createMonth = DateUtils.dateToString(date, DateUtils.PATTERN6);
//        final List<T> details = new ArrayList<>();
//        for (String month : monthList) {
//            //查询
//            S carrierData = queryData(token, phone, month);
//            if (carrierData != null) {
//                List<T> monthlist = carrierData.getData();
//                log.debug("查询运营商分库数据{}:{}月份共{}条,token:{}", getType(), month, monthlist.size(), token);
//                if (month.equals(createMonth)) {
//                    log.debug("month:{}, ,token:{}当月数据只查创建时间之前的", month, token);
//                    if (date != null) {
//                        // 任务创建月份 查询创建任务之前数据
//                        monthlist = monthlist.stream().filter(getActualFilter(date)).collect(Collectors.toList());
//                    }
//                }
//                details.addAll(monthlist);
//            }
//        }
//        log.debug("token:{},查询运营商分库数据{}:共{}条耗时:{}", token, getType(), details.size(), System.currentTimeMillis() - start);
//        return details;
//    }
//
//*
//     * 因为增量任务是新的token，而除除通话短信流量外是按token冗余，所以用token查不到历史数据，
//
//
//    private S queryHisData(String phone, String month) {
//        S data;
//        if (KEY_PHONE.equals(getKey())) {
//            data = dynamicDataBaseService.queryDataByPhone(getShardingDatabaseName(), collection, phone, month, getDetailClass());
//        } else {
//            data = dynamicDataBaseService.queryLastDataByPhone(getShardingDatabaseName(), collection, phone, month, getDetailClass());
//        }
//        if (data != null) {
//            //能用明文查到，说明数据也是明文
//            return data;
//        } else {
//            //用密文查询
//            final String enPhone = carrierCryptoUtil.encrypt(phone);
//            final S cipher = queryHisData2(enPhone, month);
//            carrierCryptoUtil.decrypt(cipher);
//            return cipher;
//        }
//    }
//
//    private S queryHisData2(String phone, String month) {
//        S data;
//        if (KEY_PHONE.equals(getKey())) {
//            data = dynamicDataBaseService.queryDataByPhone(getShardingDatabaseName(), collection, phone, month, getDetailClass());
//        } else {
//            data = dynamicDataBaseService.queryLastDataByPhone(getShardingDatabaseName(), collection, phone, month, getDetailClass());
//        }
//        return data;
//    }
//
//    private S queryData(String token, String phone, String month) {
//        if (KEY_PHONE.equals(getKey())) {
//            final S data = dynamicDataBaseService.queryDataByPhone(getShardingDatabaseName(), collection, phone, month, getDetailClass());
//            if (data != null) {
//                return data;
//            } else {
//                //用密文查询
//                final String enPhone = carrierCryptoUtil.encrypt(phone);
//                final S cipher = queryData2(enPhone, month);
//                carrierCryptoUtil.decrypt(cipher);
//                return cipher;
//            }
//        } else {
//            final S data = dynamicDataBaseService.queryDataByToken(getShardingDatabaseName(), collection, token, month, getDetailClass());
//            if (data != null) {
//                carrierCryptoUtil.decrypt(data);
//            }
//            return data;
//        }
//    }
//
//    private S queryData2(String phone, String month) {
//        if (KEY_PHONE.equals(getKey())) {
//            return dynamicDataBaseService.queryDataByPhone(getShardingDatabaseName(), collection, phone, month, getDetailClass());
//        }
//        return null;
//    }
//
//    protected CarrierData<T> newCarrierDataInstance(DataMongoObject dataMongoObject) {
//        try {
//            return getDetailClass().getDeclaredConstructor().newInstance();
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//            log.error("创建对象异常:" + getDetailClass(), e);
//            throw new RuntimeException(e);
//        }
//    }
//
//    // -------------------- abstract method --------------------
//    protected abstract CarrierPageType getType();
//
//*
//     * 短信通话流量按照月份分库，phone作为主键，不冗余
//     * 其他的按照月份分库，token作为主键，冗余
//     *
//     * @return
//
//
//    protected abstract String getKey();
//
//*
//     * 分库名字
//     *
//     * @return
//
//
//    protected abstract String getShardingDatabaseName();
//
//    protected abstract List<T> getDataDetails(CarrierInfo carrierInfo);
//
//    protected abstract Class<S> getDetailClass();
//
//*
//     * 获取历史记录汇总，用于判断是否需要合并
//     *
//     * @param carrierCrawlRecord
//     * @return
//
//
//    protected abstract Map<String, Integer> getExistingRecord(CarrierCrawlRecord carrierCrawlRecord);
//
//    protected abstract List<T> mergeData(List<T> current, List<T> exist);
//
//*
//     * 对数据分组，用于分库保存
//     *
//     * @param createTime
//     * @return
//
//
//    protected abstract Function<T, String> getGroupFunction(String createTime);
//
//*
//     * 对数据排序，用于分库保存
//     *
//     * @return
//
//
//    protected abstract Function<T, String> getSortFunction();
//
//*
//     * 过滤数据，排除任务创建时不应该存在的虚假的数据，只返回查询创建之前的数据
//     *
//     * @param date
//     * @return
//
//
//    protected abstract Predicate<? super T> getActualFilter(Date date);
//
//*
//     * 校验数据是否完整
//     *
//     * @param carrierCrawlRecord
//     * @param recordDetail
//     * @return
//
//
//    protected abstract boolean check(CarrierCrawlRecord carrierCrawlRecord, CarrierCrawlRecordDetail recordDetail, CrawlingConfigDO configDO);
//
//*
//     * 因为增量爬取，可能爬不到某个月数据
//     * 把缺失的月份补上，触发历史数据合并
//     * 把这笔增量的数据搞成全量
//     *
//     * @param details
//
//
//    protected void fillMonth(Map<String, List<T>> details) {
//        final List<String> list = DateUtil.getMonthListByDate();
//        for (String month : list) {
//            if (!details.containsKey(month)) {
//                details.put(month, Collections.emptyList());
//            }
//        }
//    }
//
//    protected List<String> getMonths(String token, CarrierCrawlRecord carrierCrawlRecord, Date createDate) {
//        // 任务创建前6个月份
//        List<String> monthList = DateUtil.getMonthListBefore6M(createDate);
//        Map<String, Integer> record = getExistingRecord(carrierCrawlRecord);
//        if (record != null) { //过滤没有记录的月份
//            monthList = monthList.stream().filter(record::containsKey).collect(Collectors.toList());
//        }
//        return monthList;
//    }
//
//    protected void prepare(List<T> details, UserParamsLog userParamsLog) {
//    }
//
//*
//     * 去掉六个月外数据，数据完整性校验，只校验最近六个月
//     *
//     * @param data
//     * @return
//
//
//    protected Map<String, Integer> filterLast6Month(Map<String, Integer> data) {
//        final List<String> months = DateUtil.getMonthListBefore6M(new Date());
//        return data.entrySet().stream()
//                .filter(e -> months.contains(e.getKey()))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//    }
//
//*
//     * 校验账单月份
//     *
//     * @param month
//
//
//    private boolean checkBillMonth(String month, String token) {
//        boolean isValidYyyyMm = DateUtil.isValidYyyyMm(month);
//        if (isValidYyyyMm) {
//            return true;
//        }
//        MailUtil.sendMail(Constant.ServerName.QZ_DATA_CENTER, token + ", 月份格式不正确", "账单月份["
//                + month + "]格式不正确,可能会导致月份缺失", MailMessageType.WARN, emailReceiver);
//        return false;
//    }
//
//    private void errorMail(String title, String content) {
//        MailUtil.sendMail(Constant.ServerName.QZ_DATA_CENTER, title, content, MailMessageType.WARN, emailReceiver);
//    }
//
//    @Autowired
//    private LoggerClient loggerClient;
//
//    protected UserParamsLog getParamLog(String token) {
//        String userParamLog = loggerClient.getTaskParam(token);
//        return ResultInfoUtil.extract(userParamLog, UserParamsLog.class);
//    }
//}
