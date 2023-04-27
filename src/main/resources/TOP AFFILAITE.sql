

Select
       vtcm.affiliateId                                                                               as affiliateId,
       vtcm.affiliateName                                                                             as affiliateName,
       vtcm.channelid                                                                                 as channelId,
       vtcm.channelName                                                                               as channelName,

       -- CLICK NUMBER
       (SELECT SUM(cc.impression)
        FROM v_widget_cpm cc
        WHERE cc.affiliateid = vtcm.affiliateid)                                                        as impressionNumber,

       -- CLICK NUMBER
       (SELECT SUM(cc.clicknumber)
        FROM v_widget_cpc cc
        WHERE cc.affiliateid = vtcm.affiliateid)                                                        as clickNumber,

       --LEAD NUMBER
       (SELECT SUM(cc.leadnumber)
        FROM v_widget_cpl cc
        WHERE cc.affiliateid = vtcm.affiliateid)                                                        as leadNumber,

       --CTR
       round(CAST(((SELECT SUM(cc.clicknumber)
                    FROM v_widget_cpc cc
                    WHERE cc.affiliateid = vtcm.affiliateid) /
                   (SELECT SUM(cc.impression)
                    FROM v_widget_cpm cc
                    WHERE cc.affiliateid = vtcm.affiliateid) * 100) AS numeric), 2)                     as CTR,

       --LR
       round(CAST(((SELECT SUM(cc.leadnumber)
                    FROM v_widget_cpl cc
                    WHERE cc.affiliateid = vtcm.affiliateid) /
                   (SELECT SUM(cc.clicknumber)
                    FROM v_widget_cpc cc
                    WHERE cc.affiliateid = vtcm.affiliateid) * 100) AS numeric), 2)                     as LR,

       --COMMISSION
       (SELECT COALESCE(SUM(cc.commssion), 0)
        FROM v_widget_cpc cc
        WHERE cc.affiliateid = vtcm.affiliateid +
                              (SELECT COALESCE(SUM(cl.commssion), 0)
                               FROM v_widget_cpl cl
                               WHERE cl.affiliateid = vtcm.affiliateid) +
                              (SELECT COALESCE(SUM(cl.commssion), 0)
                               FROM v_widget_cpl cl
                               WHERE cl.affiliateid = vtcm.affiliateid))                                as commission,

       --REVENUE
       (SELECT COALESCE(SUM(cc.revenue), 0)
        FROM v_widget_cpc cc
        WHERE cc.affiliateid = vtcm.affiliateid +
                              (SELECT COALESCE(SUM(cl.revenue), 0)
                               FROM v_widget_cpl cl
                               WHERE cl.affiliateid = vtcm.affiliateid) +
                              (SELECT COALESCE(SUM(cl.revenue), 0)
                               FROM v_widget_cpl cl
                               WHERE cl.affiliateid = vtcm.affiliateid))                                as revenue,

       --MARGINE
       round(CAST((((SELECT COALESCE(SUM(cc.revenue), 0)
                     FROM v_widget_cpc cc
                     WHERE cc.affiliateid = vtcm.affiliateid +
                                           (SELECT COALESCE(SUM(cl.revenue), 0)
                                            FROM v_widget_cpl cl
                                            WHERE cl.affiliateid = vtcm.affiliateid) +
                                           (SELECT COALESCE(SUM(cl.revenue), 0)
                                            FROM v_widget_cpl cl
                                            WHERE cl.affiliateid = vtcm.affiliateid)) -
                    (SELECT COALESCE(SUM(cc.commssion), 0)
                     FROM v_widget_cpc cc
                     WHERE cc.affiliateid = vtcm.affiliateid +
                                           (SELECT COALESCE(SUM(cl.commssion), 0)
                                            FROM v_widget_cpl cl
                                            WHERE cl.affiliateid = vtcm.affiliateid) +
                                           (SELECT COALESCE(SUM(cl.commssion), 0)
                                            FROM v_widget_cpl cl
                                            WHERE cl.affiliateid = vtcm.affiliateid)))) AS numeric), 2) as margine,

       --- MARGINE PC
       round(CAST((
               ((SELECT COALESCE(SUM(cc.revenue), 0)
                 FROM v_widget_cpc cc
                 WHERE cc.affiliateid = vtcm.affiliateid +
                                       (SELECT COALESCE(SUM(cl.revenue), 0)
                                        FROM v_widget_cpl cl
                                        WHERE cl.affiliateid = vtcm.affiliateid) +
                                       (SELECT COALESCE(SUM(cl.revenue), 0)
                                        FROM v_widget_cpl cl
                                        WHERE cl.affiliateid = vtcm.affiliateid))
                   -
                (SELECT COALESCE(SUM(cc.commssion), 0)
                 FROM v_widget_cpc cc
                 WHERE cc.affiliateid = vtcm.affiliateid +
                                       (SELECT COALESCE(SUM(cl.commssion), 0)
                                        FROM v_widget_cpl cl
                                        WHERE cl.affiliateid = vtcm.affiliateid) +
                                       (SELECT COALESCE(SUM(cl.commssion), 0)
                                        FROM v_widget_cpl cl
                                        WHERE cl.affiliateid = vtcm.affiliateid))
                   ) /
               (SELECT COALESCE(SUM(cc.revenue), 0)
                FROM v_widget_cpc cc
                WHERE cc.affiliateid = vtcm.affiliateid +
                                      (SELECT COALESCE(SUM(cl.revenue), 0)
                                       FROM v_widget_cpl cl
                                       WHERE cl.affiliateid = vtcm.affiliateid) +
                                      (SELECT COALESCE(SUM(cl.revenue), 0)
                                       FROM v_widget_cpl cl
                                       WHERE cl.affiliateid = vtcm.affiliateid))) AS NUMERIC), 2) * 100 as marginePC,

       --ECPM
       (SELECT COALESCE(SUM(tt.ecpm), 0)
        FROM v_widget_cpm tt
        WHERE tt.affiliateid = vtcm.affiliateid)                                                        as ecpm,
       --ECPC
       (SELECT COALESCE(SUM(tt.ecpc), 0)
        FROM v_widget_cpc tt
        WHERE tt.affiliateid = vtcm.affiliateid)                                                        as ecpc,
       --ECPL
       (SELECT COALESCE(SUM(tt.ecpl), 0)
        FROM v_widget_cpl tt
        WHERE tt.affiliateid = vtcm.affiliateid)                                                        as ecpl

from v_widget_cpm vtcm
         left join v_widget_cpc vtcc on vtcm.affiliateid = vtcc.affiliateid
         left join v_widget_cpl vtcl on vtcc.affiliateid = vtcl.affiliateid
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
group by  vtcm.affiliateName, vtcm.affiliateId, vtcm.channelid, vtcm.channelName;




