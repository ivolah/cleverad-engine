create or replace view public.v_top_campaign
as;

Select vtcm.campaignid,
       vtcm.campaignname,

       -- CLICK NUMBER
       (SELECT SUM(cc.impression)
        FROM v_widget_cpm cc
        WHERE cc.campaignid = vtcm.campaignid)                                                        as impressionNumber,

       -- CLICK NUMBER
       (SELECT SUM(cc.clicknumber)
        FROM v_widget_cpc cc
        WHERE cc.campaignid = vtcm.campaignid)                                                        as clickNumber,

       --LEAD NUMBER
       (SELECT SUM(cc.leadnumber)
        FROM v_widget_cpl cc
        WHERE cc.campaignid = vtcm.campaignid)                                                        as leadNumber,

       --CTR
       round(CAST(((SELECT SUM(cc.clicknumber)
                    FROM v_widget_cpc cc
                    WHERE cc.campaignid = vtcm.campaignid) /
                   (SELECT SUM(cc.impression)
                    FROM v_widget_cpm cc
                    WHERE cc.campaignid = vtcm.campaignid) * 100) AS numeric), 2)                     as CTR,

       --LR
       round(CAST(((SELECT SUM(cc.leadnumber)
                    FROM v_widget_cpl cc
                    WHERE cc.campaignid = vtcm.campaignid) /
                   (SELECT SUM(cc.clicknumber)
                    FROM v_widget_cpc cc
                    WHERE cc.campaignid = vtcm.campaignid) * 100) AS numeric), 2)                     as LR,

       --COMMISSION
       (SELECT COALESCE(SUM(cc.commssion), 0)
        FROM v_widget_cpc cc
        WHERE cc.campaignid = vtcm.campaignid +
                              (SELECT COALESCE(SUM(cl.commssion), 0)
                               FROM v_widget_cpl cl
                               WHERE cl.campaignid = vtcm.campaignid) +
                              (SELECT COALESCE(SUM(cl.commssion), 0)
                               FROM v_widget_cpl cl
                               WHERE cl.campaignid = vtcm.campaignid))                                as commission,

       --REVENUE
       (SELECT COALESCE(SUM(cc.revenue), 0)
        FROM v_widget_cpc cc
        WHERE cc.campaignid = vtcm.campaignid +
                              (SELECT COALESCE(SUM(cl.revenue), 0)
                               FROM v_widget_cpl cl
                               WHERE cl.campaignid = vtcm.campaignid) +
                              (SELECT COALESCE(SUM(cl.revenue), 0)
                               FROM v_widget_cpl cl
                               WHERE cl.campaignid = vtcm.campaignid))                                as revenue,

       --MARGINE
       round(CAST((((SELECT COALESCE(SUM(cc.revenue), 0)
                     FROM v_widget_cpc cc
                     WHERE cc.campaignid = vtcm.campaignid +
                                           (SELECT COALESCE(SUM(cl.revenue), 0)
                                            FROM v_widget_cpl cl
                                            WHERE cl.campaignid = vtcm.campaignid) +
                                           (SELECT COALESCE(SUM(cl.revenue), 0)
                                            FROM v_widget_cpl cl
                                            WHERE cl.campaignid = vtcm.campaignid)) -
                    (SELECT COALESCE(SUM(cc.commssion), 0)
                     FROM v_widget_cpc cc
                     WHERE cc.campaignid = vtcm.campaignid +
                                           (SELECT COALESCE(SUM(cl.commssion), 0)
                                            FROM v_widget_cpl cl
                                            WHERE cl.campaignid = vtcm.campaignid) +
                                           (SELECT COALESCE(SUM(cl.commssion), 0)
                                            FROM v_widget_cpl cl
                                            WHERE cl.campaignid = vtcm.campaignid)))) AS numeric), 2) as margine,

       --- MARGINE PC
       round(CAST((
               ((SELECT COALESCE(SUM(cc.revenue), 0)
                 FROM v_widget_cpc cc
                 WHERE cc.campaignid = vtcm.campaignid +
                                       (SELECT COALESCE(SUM(cl.revenue), 0)
                                        FROM v_widget_cpl cl
                                        WHERE cl.campaignid = vtcm.campaignid) +
                                       (SELECT COALESCE(SUM(cl.revenue), 0)
                                        FROM v_widget_cpl cl
                                        WHERE cl.campaignid = vtcm.campaignid))
                   -
                (SELECT COALESCE(SUM(cc.commssion), 0)
                 FROM v_widget_cpc cc
                 WHERE cc.campaignid = vtcm.campaignid +
                                       (SELECT COALESCE(SUM(cl.commssion), 0)
                                        FROM v_widget_cpl cl
                                        WHERE cl.campaignid = vtcm.campaignid) +
                                       (SELECT COALESCE(SUM(cl.commssion), 0)
                                        FROM v_widget_cpl cl
                                        WHERE cl.campaignid = vtcm.campaignid))
                   ) /
               (SELECT COALESCE(SUM(cc.revenue), 0)
                FROM v_widget_cpc cc
                WHERE cc.campaignid = vtcm.campaignid +
                                      (SELECT COALESCE(SUM(cl.revenue), 0)
                                       FROM v_widget_cpl cl
                                       WHERE cl.campaignid = vtcm.campaignid) +
                                      (SELECT COALESCE(SUM(cl.revenue), 0)
                                       FROM v_widget_cpl cl
                                       WHERE cl.campaignid = vtcm.campaignid))) AS NUMERIC), 2) * 100 as marginePC,

       --ECPM
       (SELECT COALESCE(SUM(tt.ecpm), 0)
        FROM v_widget_cpm tt
        WHERE tt.campaignid = vtcm.campaignid)                                                        as ecpm,
       --ECPC
       (SELECT COALESCE(SUM(tt.ecpc), 0)
        FROM v_widget_cpc tt
        WHERE tt.campaignid = vtcm.campaignid)                                                        as ecpc,
       --ECPL
       (SELECT COALESCE(SUM(tt.ecpl), 0)
        FROM v_widget_cpl tt
        WHERE tt.campaignid = vtcm.campaignid)                                                        as ecpl

from v_widget_cpm vtcm
         left join v_widget_cpc vtcc on vtcm.campaignid = vtcc.campaignid
         left join v_widget_cpl vtcl on vtcc.campaignid = vtcl.campaignid
where (
        ((:dateFrom < vtcm.date) AND (:dateTo > vtcm.date)) or
        ((:dateFrom < vtcc.date) AND (:dateTo > vtcc.date)) or
        ((:dateFrom < vtcl.date) AND (:dateTo > vtcl.date))
    )
  AND (
        (vtcm.dictionaryId in (:dictionaryList)) or
        (vtcc.dictionaryId in (:dictionaryList)) or
        (vtcl.dictionaryId in (:dictionaryList))
    )
group by vtcm.campaignid, vtcm.campaignname;




