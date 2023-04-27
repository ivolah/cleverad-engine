package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpc;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpl;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCpm;
import it.cleverad.engine.persistence.model.service.WidgetCampaignDayCps;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCpcRepository;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCplRepository;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCpmRepository;
import it.cleverad.engine.persistence.repository.service.WidgetCampaignDayCpsRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.threeten.extra.YearWeek;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class ViewBusiness {

    @Autowired
    private WidgetCampaignDayCpcRepository widgetCampaignDayCpcRepository;
    @Autowired
    private WidgetCampaignDayCpmRepository widgetCampaignDayCpmRepository;
    @Autowired
    private WidgetCampaignDayCplRepository widgetCampaignDayCplRepository;
    @Autowired
    private WidgetCampaignDayCpsRepository widgetCampaignDayCpsRepository;

    /**
     * ===========================================================================================================
     * >>> CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC <<<
     * =================================================================<==========================================
     **/

    /// CPC CAMPAIGN DAY
    public Page<WidgetCampaignDayCpc> getStatCampaignDayCpc(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        return widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageable);
    }

    public Page<WidgetCampaignDayCpc> getTopCampaignsDayCpc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("totale")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        log.info("week :: " + week);
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        return widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageable);
    }

    public String getWidgetCampaignsDayCpc() {

        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        Page<WidgetCampaignDayCpc> tutto = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpc::getDoy).collect(Collectors.toSet());
        Set<String> camps = tutto.stream().map(WidgetCampaignDayCpc::getCampaign).collect(Collectors.toSet());

        Set<Long> doysDaVerificare = new HashSet<>();
        for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++) {
            doysDaVerificare.add(i);
        }

        JSONObject mainObj = new JSONObject();
        JSONArray arr = new JSONArray();
        camps.stream().distinct().forEach(campagna -> {
            JSONObject ele = new JSONObject();
            ele.put("name", campagna);
            JSONArray ja = new JSONArray();

            doysDaVerificare.stream().sorted().forEach(gg -> {
                Double dd = 0D;

                Filter filter = new Filter();
                filter.setDoy(gg);
                filter.setCampaign(campagna);
                List<WidgetCampaignDayCpc> giornato = widgetCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(filter), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
                if (giornato.size() > 0) {
                    dd = giornato.get(0).getTotale();
                }

                ja.put(dd);
            });

            ele.put("data", ja);
            arr.put(ele);
            //log.info(aLong + " :: " + dd);
        });

        mainObj.put("series", arr);
        return mainObj.toString();

//        doys.stream().forEach(doy -> {
//            log.info("D0Y {} :: {}", doy, multiValue.get(doy).size());
//            multiValue.get(doy).stream().filter(s -> s.getDoy().equals(doy)).forEach(s -> log.info(s.getCampaign() + " " + s.getTotale()));
//        });

//        MultiValuedMap<String, Double> valori = new ArrayListValuedHashMap<>();
//        statCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageable).forEach(statCampaignDayCpc -> {
//            valori.put(statCampaignDayCpc.getCampaign(), statCampaignDayCpc.getTotale());
//        });
//
//        JSONObject mainObj = new JSONObject();
//        JSONArray arr = new JSONArray();
//        valori.keys().stream().distinct().forEach(s -> {
//            JSONObject ele = new JSONObject();
//            ele.put("name", s);
//            JSONArray ja = new JSONArray();
//            valori.get(s).forEach(aDouble -> ja.put(aDouble));
//            ele.put("data", ja);
//            arr.put(ele);
//        });


    }


    private Specification<WidgetCampaignDayCpc> getSpecificationCampaignDayCpc(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getCampaign() != null) {
                predicates.add(cb.equal(root.get("campaign"), request.getCampaign()));
            }

            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }

            if (request.getDoy() != null) {
                predicates.add(cb.equal(root.get("doy"), request.getDoy()));
            }

            if (request.getDoyMenoDieci() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("doy"), request.getDoyMenoDieci()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }


    /**
     * ===========================================================================================================
     * >>> CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM <<<
     * ===========================================================================================================
     **/

    public Page<WidgetCampaignDayCpm> getStatCampaignDayCpm(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        return widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), pageable);
    }

    public Page<WidgetCampaignDayCpm> getTopCampaignsDayCpm() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("totale")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        return widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), pageable);
    }

    public String getWidgetCampaignsDayCpm() {

        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        Page<WidgetCampaignDayCpm> tutto = widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpm::getDoy).collect(Collectors.toSet());
        Set<String> camps = tutto.stream().map(WidgetCampaignDayCpm::getCampaign).collect(Collectors.toSet());

        Set<Long> doysDaVerificare = new HashSet<>();
        for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++) {
            doysDaVerificare.add(i);
        }

        JSONObject mainObj = new JSONObject();
        JSONArray tutti = new JSONArray();
        camps.stream().distinct().forEach(campagna -> {
            JSONObject ele = new JSONObject();
            ele.put("name", campagna);
            JSONArray arrray = new JSONArray();
            doysDaVerificare.stream().sorted().forEach(gg -> {
                Double dd = 0D;
                Filter filter = new Filter();
                filter.setDoy(gg);
                filter.setCampaign(campagna);
                List<WidgetCampaignDayCpm> giornato = widgetCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(filter), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
                if (giornato.size() > 0) {
                    dd = giornato.get(0).getTotale();
                }
                log.info("campagna :: {} :: {} :: {}", campagna, gg, dd);
                arrray.put(dd);
            });

            ele.put("data", arrray);
            tutti.put(ele);
        });

        mainObj.put("series", tutti);
        return mainObj.toString();
    }

    private Specification<WidgetCampaignDayCpm> getSpecificationCampaignDayCpm(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }
            if (request.getDoy() != null) {
                predicates.add(cb.equal(root.get("doy"), request.getDoy()));
            }
            if (request.getDoyMenoDieci() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("doy"), request.getDoyMenoDieci()));
            }
            if (request.getCampaign() != null) {
                predicates.add(cb.equal(root.get("campaign"), request.getCampaign()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }


    /**
     * ===========================================================================================================
     * >>> CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL <<<
     * ===========================================================================================================
     **/

    public Page<WidgetCampaignDayCpl> getStatCampaignDayCpl(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        return widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), pageable);
    }

    public Page<WidgetCampaignDayCpl> getTopCampaignsDayCpl() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("valore")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        return widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), pageable);
    }

    public String getWidgetCampaignsDayCpl() {
        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        Page<WidgetCampaignDayCpl> tutto = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCpl::getDoy).collect(Collectors.toSet());
        Set<String> camps = tutto.stream().map(WidgetCampaignDayCpl::getCampaign).collect(Collectors.toSet());

        Set<Long> doysDaVerificare = new HashSet<>();
        for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++) {
            doysDaVerificare.add(i);
        }

        JSONObject mainObj = new JSONObject();
        JSONArray arr = new JSONArray();
        camps.stream().distinct().forEach(campagna -> {
            JSONObject ele = new JSONObject();
            ele.put("name", campagna);
            JSONArray ja = new JSONArray();

            doysDaVerificare.stream().sorted().forEach(gg -> {
                Double dd = 0D;

                Filter filter = new Filter();
                filter.setDoy(gg);
                filter.setCampaign(campagna);
                List<WidgetCampaignDayCpl> giornato = widgetCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(filter), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
                if (giornato.size() > 0) {
                    dd = giornato.get(0).getValore();
                }

                ja.put(dd);
            });

            ele.put("data", ja);
            arr.put(ele);
            //log.info(aLong + " :: " + dd);
        });

        mainObj.put("series", arr);
        return mainObj.toString();
    }

    private Specification<WidgetCampaignDayCpl> getSpecificationCampaignDayCpl(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }
            if (request.getCampaign() != null) {
                predicates.add(cb.equal(root.get("campaign"), request.getCampaign()));
            }
            if (request.getDoy() != null) {
                predicates.add(cb.equal(root.get("doy"), request.getDoy()));
            }            if (request.getDoyMenoDieci() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("doy"), request.getDoyMenoDieci()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    /**
     * ===========================================================================================================
     * >>> CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS <<<
     * ===========================================================================================================
     **/

    public Page<WidgetCampaignDayCps> getStatCampaignDayCps(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        return widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), pageable);
    }

    public Page<WidgetCampaignDayCps> getTopCampaignsDayCps() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("valore")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        return widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), pageable);
    }

    public String getWidgetCampaignsDayCps() {
        Filter request = new Filter();
        request.setDoyMenoDieci(LocalDate.now().getDayOfYear() - 11);
        Page<WidgetCampaignDayCps> tutto = widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy"))));

        Set<Long> doys = tutto.stream().map(WidgetCampaignDayCps::getDoy).collect(Collectors.toSet());
        Set<String> camps = tutto.stream().map(WidgetCampaignDayCps::getCampaign).collect(Collectors.toSet());

        Set<Long> doysDaVerificare = new HashSet<>();
        for (Long i = doys.stream().min(Long::compareTo).get(); i <= doys.stream().max(Long::compareTo).get(); i++) {
            doysDaVerificare.add(i);
        }

        JSONObject mainObj = new JSONObject();
        JSONArray arr = new JSONArray();
        camps.stream().distinct().forEach(campagna -> {
            JSONObject ele = new JSONObject();
            ele.put("name", campagna);
            JSONArray ja = new JSONArray();

            doysDaVerificare.stream().sorted().forEach(gg -> {
                Double dd = 0D;

                Filter filter = new Filter();
                filter.setDoy(gg);
                filter.setCampaign(campagna);
                List<WidgetCampaignDayCps> giornato = widgetCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(filter), PageRequest.of(0, 1000, Sort.by(Sort.Order.asc("doy")))).stream().collect(Collectors.toList());
                if (giornato.size() > 0) {
                    dd = giornato.get(0).getValore();
                }

                ja.put(dd);
            });

            ele.put("data", ja);
            arr.put(ele);
            //log.info(aLong + " :: " + dd);
        });

        mainObj.put("series", arr);
        return mainObj.toString();
    }

    private Specification<WidgetCampaignDayCps> getSpecificationCampaignDayCps(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }
            if (request.getCampaign() != null) {
                predicates.add(cb.equal(root.get("campaign"), request.getCampaign()));
            }
            if (request.getDoy() != null) {
                predicates.add(cb.equal(root.get("doy"), request.getDoy()));
            }
            if (request.getDoyMenoDieci() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("doy"), request.getDoyMenoDieci()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

//    public Page<StatCPLClickCampaignMediaDay> searchStatCpcClickCampaignMediaDay(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcClickCampaignMediaDay> page = statCpcClickCampaignMediaDayRepository.findAll(getSpecificationCpcCampaignDay(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcClickCampaignMediaDay> getSpecificationStatCpcClickCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    /**
//     * ============================================================================================================
//     **/
//
//    public Page<StatCpcValueCampaign> searchStatCpcValueCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcValueCampaign> page = statCpcValueCampaignRepository.findAll(getSpecificationStatCpcValueCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcValueCampaign> getSpecificationStatCpcValueCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    public Page<StatCpcValueCampaignWeek> searchStatCpcValueCampaignWeek(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcValueCampaignWeek> page = statCpcValueCampaignWeekRepository.findAll(getSpecificationStatCpcValueCampaignWeek(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcValueCampaignWeek> getSpecificationStatCpcValueCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    /**
//     * ============================================================================================================
//     **/
//
//    public Page<StatCpcTransactionCampaignMedia> searchStatCpcTransactionCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcTransactionCampaignMedia> page = statCpcTransactionCampaignRepository.findAll(getSpecificationStatCpcTransactionCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcTransactionCampaignMedia> getSpecificationStatCpcTransactionCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//        /**
//     * ============================================================================================================
//     **/
//
//    public Page<StatCplValueCampaign> searchStatCplValueCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCplValueCampaign> page = statCplValueCampaignRepository.findAll(getSpecificationStatCplValueCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCplValueCampaign> getSpecificationStatCplValueCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    public Page<StatCplValueCampaignWeek> searchStatCplValueCampaignWeek(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCplValueCampaignWeek> page = statCplValueCampaignWeekRepository.findAll(getSpecificationStatCplValueCampaignWeek(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCplValueCampaignWeek> getSpecificationStatCplValueCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String description;

        private Long campaignId;
        private String campaign;
        private Long year;
        private Long month;
        private Long day;
        private Long doy;
        private Integer week;
        private Integer weekMeno;
        private Integer doyMenoDieci;

    }

}
