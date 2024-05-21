package it.cleverad.engine.persistence.model.service;

import it.cleverad.engine.web.dto.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NamedNativeQuery(name = "Report.searchReportDaily",
        query = "With trans as (SELECT row_number() OVER ()                                                     AS rn,  " +
                "                      CAST('CPC' as text)                                                      AS tipo,  " +
                "                      CAST(t.id as bigint)                                                     AS id,  " +
                "                      t.creation_date                                                          AS creationdate,  " +
                "                      date(t.date_time)                                                        AS datetime,  " +
                "                      CAST(t.status_id as bigint)                                              AS statusid,  " +
                "                      tdd.name                                                                 AS statusname,  " +
                "                      CAST(t.dictionary_id as bigint)                                          AS dictionaryid,  " +
                "                      td.name                                                                  AS dictionaryname,  " +
                "                      CAST(t.affiliate_id as bigint)                                           AS affiliateid,  " +
                "                      ta.name                                                                  AS affiliatename,  " +
                "                      CAST(tadv.id as bigint)                                                  AS advertiserid,  " +
                "                      tadv.name                                                                AS advertisername,  " +
                "                      CAST(t.channel_id as bigint)                                             AS channelid,  " +
                "                      c.name                                                                   AS channelname,  " +
                "                      CAST(t.campaign_id as bigint)                                            AS campaignid,  " +
                "                      tc.name                                                                  AS campaignname,  " +
                "                      CAST(t.media_id as bigint)                                               AS mediaid,  " +
                "                      CAST(t.commission_id as bigint)                                          AS commissionid,  " +
                "                      tco.name                                                                 AS commissionname,  " +
                "                      tco.value                                                                AS commissionvalue,  " +
                "                      CAST(0 as numeric)                                                       AS commissionvaluerigettato,  " +
                "                      round(CAST(t.value AS numeric), 2)                                       AS value,  " +
                "                      CAST(0 as numeric)                                                       AS valuerigettato,  " +
                "                      CAST(t.revenue_id as bigint)                                             AS revenueid,  " +
                "                      trf.revenue                                                              AS revenuevalue,  " +
                "                      CAST(0 as numeric)                                                       AS revenuevaluerigettato,  " +
                "                      round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue,  " +
                "                      CAST(0 as numeric)                                                       AS revenuerigettato,  " +
                "                      t.click_number                                                           AS clicknumber,  " +
                "                      CAST(0 as bigint)                                                        AS clicknumberrigettato,  " +
                "                      CAST(0 as bigint)                                                        AS impressionnumber,  " +
                "                      CAST(0 as bigint)                                                        AS leadnumber,  " +
                "                      CAST(0 as bigint)                                                        AS leadnumberrigettato,  " +
                "                      CAST(NULL AS character varying)                                          AS data,  " +
                "                      CAST(t.wallet_id as bigint)                                              AS walletid,  " +
                "                      t.payout_present                                                         AS payoutpresent,  " +
                "                      CAST(t.payout_id as bigint)                                              AS payoutid,  " +
                "                      t.payout_reference                                                       AS payoutreference  " +
                "               FROM t_transaction_cpc as t  " +
                "                        LEFT JOIN t_campaign tc ON t.campaign_id = tc.id  " +
                "                        LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id  " +
                "                        LEFT JOIN t_commision tco ON t.commission_id = tco.id  " +
                "                        LEFT JOIN t_channel c ON t.channel_id = c.id  " +
                "                        LEFT JOIN t_dictionary td ON t.dictionary_id = td.id  " +
                "                        LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id  " +
                "                        LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id  " +
                "                        LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id  " +
                "               WHERE (t.status_id = 72  " +
                "                   OR t.status_id = 73)  " +
                "                 AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time))  " +
                "                 AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time))  " +
                "                 AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint)))  " +
                "                 AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint)))  " +
                "                 AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint)))  " +
                "                 AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint)))  " +
                "                 AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint)))  " +
                "                  AND ((:dictionaryId)  IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId)))  " +
                "               UNION  " +
                "               SELECT row_number() OVER ()                                                     AS rn,  " +
                "                      CAST('CPC' AS text)                                                      AS tipo,  " +
                "                      CAST(t.id as bigint)                                                     AS id,  " +
                "                      t.creation_date                                                          AS creationdate,  " +
                "                      date(t.date_time)                                                        AS datetime,  " +
                "                      CAST(t.status_id as bigint)                                              AS statusid,  " +
                "                      tdd.name                                                                 AS statusname,  " +
                "                      CAST(t.dictionary_id as bigint)                                          AS dictionaryid,  " +
                "                      td.name                                                                  AS dictionaryname,  " +
                "                      CAST(t.affiliate_id as bigint)                                           AS affiliateid,  " +
                "                      ta.name                                                                  AS affiliatename,  " +
                "                      CAST(tadv.id as bigint)                                                  AS advertiserid,  " +
                "                      tadv.name                                                                AS advertisername,  " +
                "                      CAST(t.channel_id as bigint)                                             AS channelid,  " +
                "                      c.name                                                                   AS channelname,  " +
                "                      CAST(t.campaign_id as bigint)                                            AS campaignid,  " +
                "                      tc.name                                                                  AS campaignname,  " +
                "                      CAST(t.media_id as bigint)                                               AS mediaid,  " +
                "                      CAST(t.commission_id as bigint)                                          AS commissionid,  " +
                "                      tco.name                                                                 AS commissionname,  " +
                "                      0                                                                        AS commissionvalue,  " +
                "                      tco.value                                                                AS commissionvaluerigettato,  " +
                "                      0                                                                        AS value,  " +
                "                      round(CAST(t.value AS numeric), 2)                                       AS valuerigettato,  " +
                "                      t.revenue_id                                                             AS revenueid,  " +
                "                      0                                                                        AS revenuevalue,  " +
                "                      trf.revenue                                                              AS revenuevaluerigettato,  " +
                "                      0                                                                        AS revenue,  " +
                "                      round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenuerigettato,  " +
                "                      0                                                                        AS clicknumber,  " +
                "                      t.click_number                                                           AS clicknumberrigettato,  " +
                "                      0                                                                        AS impressionnumber,  " +
                "                      0                                                                        AS leadnumber,  " +
                "                      0                                                                        AS leadnumberrigettato,  " +
                "                      CAST(NULL AS character varying)                                          AS data,  " +
                "                      CAST(t.wallet_id as bigint)                                              AS walletid,  " +
                "                      t.payout_present                                                         AS payoutpresent,  " +
                "                      CAST(t.payout_id as bigint)                                              AS payoutid,  " +
                "                      t.payout_reference                                                       AS payoutreference  " +
                "               FROM t_transaction_cpc t  " +
                "                        LEFT JOIN t_campaign tc ON t.campaign_id = tc.id  " +
                "                        LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id  " +
                "                        LEFT JOIN t_commision tco ON t.commission_id = tco.id  " +
                "                        LEFT JOIN t_channel c ON t.channel_id = c.id  " +
                "                        LEFT JOIN t_dictionary td ON t.dictionary_id = td.id  " +
                "                        LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id  " +
                "                        LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id  " +
                "                        LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id  " +
                "               WHERE (t.status_id = 74  " +
                "                   OR t.status_id = 70)  " +
                "                 AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time))  " +
                "                 AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time))  " +
                "                 AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint)))  " +
                "                 AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint)))  " +
                "                 AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint)))  " +
                "                 AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint)))  " +
                "                 AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint)))  " +
                "                  AND ((:dictionaryId)  IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId)))  " +
                "               UNION  " +
                "               SELECT row_number() OVER ()                                                    AS rn,  " +
                "                      CAST('CPL' AS text)                                                     AS tipo,  " +
                "                      CAST(t.id as bigint)                                                    AS id,  " +
                "                      t.creation_date                                                         AS creationdate,  " +
                "                      date(t.date_time)                                                       AS datetime,  " +
                "                      CAST(t.status_id as bigint)                                             AS statusid,  " +
                "                      tdd.name                                                                AS statusname,  " +
                "                      CAST(t.dictionary_id as bigint)                                         AS dictionaryid,  " +
                "                      td.name                                                                 AS dictionaryname,  " +
                "                      CAST(t.affiliate_id as bigint)                                          AS affiliateid,  " +
                "                      ta.name                                                                 AS affiliatename,  " +
                "                      CAST(tadv.id as bigint)                                                 AS advertiserid,  " +
                "                      tadv.name                                                               AS advertisername,  " +
                "                      CAST(t.channel_id as bigint)                                            AS channelid,  " +
                "                      c.name                                                                  AS channelname,  " +
                "                      CAST(t.campaign_id as bigint)                                           AS campaignid,  " +
                "                      tc.name                                                                 AS campaignname,  " +
                "                      CAST(t.media_id as bigint)                                              AS mediaid,  " +
                "                      CAST(t.commission_id as bigint)                                         AS commissionid,  " +
                "                      tco.name                                                                AS commissionname,  " +
                "                      tco.value                                                               AS commissionvalue,  " +
                "                      0                                                                       AS commissionvaluerigettato,  " +
                "                      round(CAST(t.value AS numeric), 2)                                      AS value,  " +
                "                      0                                                                       AS valuerigettato,  " +
                "                      t.revenue_id                                                            AS revenueid,  " +
                "                      trf.revenue                                                             AS revenuevalue,  " +
                "                      0                                                                       AS revenuevaluerigettato,  " +
                "                      round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue,  " +
                "                      0                                                                       AS revenuerigettato,  " +
                "                      0                                                                       AS clicknumber,  " +
                "                      0                                                                       AS clicknumberrigettato,  " +
                "                      0                                                                       AS impressionnumber,  " +
                "                      1                                                                       AS leadnumber,  " +
                "                      0                                                                       AS leadnumberrigettato,  " +
                "                      t.data                                                                  AS data,  " +
                "                      CAST(t.wallet_id as bigint)                                             AS walletid,  " +
                "                      t.payout_present                                                        AS payoutpresent,  " +
                "                      CAST(t.payout_id as bigint)                                             AS payoutid,  " +
                "                      t.payout_reference                                                      AS payoutreference  " +
                "               FROM t_transaction_cpl t  " +
                "                        LEFT JOIN t_campaign tc ON t.campaign_id = tc.id  " +
                "                        LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id  " +
                "                        LEFT JOIN t_commision tco ON t.commission_id = tco.id  " +
                "                        LEFT JOIN t_channel c ON t.channel_id = c.id  " +
                "                        LEFT JOIN t_dictionary td ON t.dictionary_id = td.id  " +
                "                        LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id  " +
                "                        LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id  " +
                "                        LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id  " +
                "               WHERE (t.status_id = 72  " +
                "                   OR t.status_id = 73)  " +
                "                 AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time))  " +
                "                 AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time))  " +
                "                 AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint)))  " +
                "                 AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint)))  " +
                "                 AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint)))  " +
                "                 AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint)))  " +
                "                 AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint)))  " +
                "                  AND ((:dictionaryId)  IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId)))  " +
                "               UNION  " +
                "               SELECT row_number() OVER ()                                                    AS rn,  " +
                "                      CAST('CPL' AS text)                                                     AS tipo,  " +
                "                      CAST(t.id as bigint)                                                    AS id,  " +
                "                      t.creation_date                                                         AS creationdate,  " +
                "                      date(t.date_time)                                                       AS datetime,  " +
                "                      CAST(t.status_id as bigint)                                             AS statusid,  " +
                "                      tdd.name                                                                AS statusname,  " +
                "                      CAST(t.dictionary_id as bigint)                                         AS dictionaryid,  " +
                "                      td.name                                                                 AS dictionaryname,  " +
                "                      CAST(t.affiliate_id as bigint)                                          AS affiliateid,  " +
                "                      ta.name                                                                 AS affiliatename,  " +
                "                      CAST(tadv.id as bigint)                                                 AS advertiserid,  " +
                "                      tadv.name                                                               AS advertisername,  " +
                "                      CAST(t.channel_id as bigint)                                            AS channelid,  " +
                "                      c.name                                                                  AS channelname,  " +
                "                      CAST(t.campaign_id as bigint)                                           AS campaignid,  " +
                "                      tc.name                                                                 AS campaignname,  " +
                "                      CAST(t.media_id as bigint)                                              AS mediaid,  " +
                "                      CAST(t.commission_id as bigint)                                         AS commissionid,  " +
                "                      tco.name                                                                AS commissionname,  " +
                "                      0                                                                       AS commissionvalue,  " +
                "                      tco.value                                                               AS commissionvaluerigettato,  " +
                "                      0                                                                       AS value,  " +
                "                      round(CAST(t.value AS numeric), 2)                                      AS valuerigettato,  " +
                "                      t.revenue_id                                                            AS revenueid,  " +
                "                      0                                                                       AS revenuevalue,  " +
                "                      trf.revenue                                                             AS revenuevaluerigettato,  " +
                "                      0                                                                       AS revenue,  " +
                "                      round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenuerigettato,  " +
                "                      0                                                                       AS clicknumber,  " +
                "                      0                                                                       AS clicknumberrigettato,  " +
                "                      0                                                                       AS impressionnumber,  " +
                "                      0                                                                       AS leadnumber,  " +
                "                      1                                                                       AS leadnumberrigettato,  " +
                "                      t.data                                                                  AS data,  " +
                "                      CAST(t.wallet_id as bigint)                                             AS walletid,  " +
                "                      t.payout_present                                                        AS payoutpresent,  " +
                "                      CAST(t.payout_id as bigint)                                             AS payoutid,  " +
                "                      t.payout_reference                                                      AS payoutreference  " +
                "               FROM t_transaction_cpl t  " +
                "                        LEFT JOIN t_campaign tc ON t.campaign_id = tc.id  " +
                "                        LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id  " +
                "                        LEFT JOIN t_commision tco ON t.commission_id = tco.id  " +
                "                        LEFT JOIN t_channel c ON t.channel_id = c.id  " +
                "                        LEFT JOIN t_dictionary td ON t.dictionary_id = td.id  " +
                "                        LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id  " +
                "                        LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id  " +
                "                        LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id  " +
                "               WHERE (t.status_id = 74  " +
                "                   OR t.status_id = 70)  " +
                "                 AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time))  " +
                "                 AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time))  " +
                "                 AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint)))  " +
                "                 AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint)))  " +
                "                 AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint)))  " +
                "                 AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint)))  " +
                "                 AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint)))  " +
                "                  AND ((:dictionaryId)  IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId)))  " +
                "               UNION  " +
                "               SELECT row_number() OVER ()                                                          AS rn,  " +
                "                      CAST('CPM' AS text)                                                           AS tipo,  " +
                "                      CAST(t.id as bigint)                                                          AS id,  " +
                "                      t.creation_date                                                               AS creationdate,  " +
                "                      date(t.date_time)                                                             AS datetime,  " +
                "                      CAST(t.status_id as bigint)                                                   AS statusid,  " +
                "                      tdd.name                                                                      AS statusname,  " +
                "                      CAST(t.dictionary_id as bigint)                                               AS dictionaryid,  " +
                "                      td.name                                                                       AS dictionaryname,  " +
                "                      CAST(t.affiliate_id as bigint)                                                AS affiliateid,  " +
                "                      ta.name                                                                       AS affiliatename,  " +
                "                      CAST(tadv.id as bigint)                                                       AS advertiserid,  " +
                "                      tadv.name                                                                     AS advertisername,  " +
                "                      CAST(t.channel_id as bigint)                                                  AS channelid,  " +
                "                      c.name                                                                        AS channelname,  " +
                "                      CAST(t.campaign_id as bigint)                                                 AS campaignid,  " +
                "                      tc.name                                                                       AS campaignname,  " +
                "                      CAST(t.media_id as bigint)                                                    AS mediaid,  " +
                "                      CAST(t.commission_id as bigint)                                               AS commissionid,  " +
                "                      tco.name                                                                      AS commissionname,  " +
                "                      tco.value                                                                     AS commissionvalue,  " +
                "                      0                                                                             AS commissionvaluerigettato,  " +
                "                      round(CAST(t.value AS numeric), 2)                                            AS value,  " +
                "                      0                                                                             AS valuerigettato,  " +
                "                      t.revenue_id                                                                  AS revenueid,  " +
                "                      trf.revenue                                                                   AS revenuevalue,  " +
                "                      0                                                                             AS revenuevaluerigettato,  " +
                "                      round(CAST(trf.revenue AS numeric) * CAST(t.impression_number AS numeric), 2) AS revenue,  " +
                "                      0                                                                             AS revenuerigettato,  " +
                "                      0                                                                             AS clicknumber,  " +
                "                      0                                                                             AS clicknumberrigettato,  " +
                "                      t.impression_number                                                           AS impressionnumber,  " +
                "                      0                                                                             AS leadnumber,  " +
                "                      0                                                                             AS leadnumberrigettato,  " +
                "                      CAST(NULL AS character varying)                                               AS data,  " +
                "                      CAST(t.wallet_id as bigint)                                                   AS walletid,  " +
                "                      t.payout_present                                                              AS payoutpresent,  " +
                "                      CAST(t.payout_id as bigint)                                                   AS payoutid,  " +
                "                      t.payout_reference                                                            AS payoutreference  " +
                "               FROM t_transaction_cpm t  " +
                "                        LEFT JOIN t_campaign tc ON t.campaign_id = tc.id  " +
                "                        LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id  " +
                "                        LEFT JOIN t_commision tco ON t.commission_id = tco.id  " +
                "                        LEFT JOIN t_channel c ON t.channel_id = c.id  " +
                "                        LEFT JOIN t_dictionary td ON t.dictionary_id = td.id  " +
                "                        LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id  " +
                "                        LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id  " +
                "                        LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id  " +
                "               WHERE (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time))  " +
                "                 AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time))  " +
                "                 AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint)))  " +
                "                 AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint)))  " +
                "                 AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint)))  " +
                "                 AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint)))  " +
                "                 AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint)))  " +
                "                 AND ((:dictionaryId)  IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint)))  " +
                "                 AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))))  " +
                "SELECT trans.datetime                                                                                            as giorno,  " +
                "       COALESCE(SUM(trans.impressionnumber), 0)                                                                  as impressionNumber,  " +
                "       COALESCE(SUM(trans.clicknumber), 0)                                                                       as clickNumber,  " +
                "       COALESCE(SUM(trans.clicknumberrigettato), 0)                                                              as clickNumberRigettato,  " +
                "       COALESCE(SUM(trans.leadnumber), 0)                                                                        as leadNumber,  " +
                "       COALESCE(SUM(trans.leadnumberrigettato), 0)                                                               as leadNumberRigettato,  " +
                "       COALESCE(round(SUM(trans.clicknumber) / NULLIF(SUM(trans.impressionnumber), 0) * 100, 2), 0)                         as CTR,  " +
                "       COALESCE(round(CAST((SUM(trans.leadnumber) / NULLIF(SUM(trans.clicknumber), 0) * 100) AS numeric), 2), 0) as LR,  " +
                "       COALESCE(round(CAST(SUM(trans.value) AS numeric), 2), 0)                                                  as commission,  " +
                "       COALESCE(round(CAST(SUM(trans.valuerigettato) AS numeric), 2), 0)                                         as commissionRigettato,  " +
                "       COALESCE(round(CAST(SUM(trans.revenue) AS numeric), 2), 0)                                                as revenue,  " +
                "       COALESCE(round(CAST(SUM(trans.revenuerigettato) AS numeric), 2), 0)                                       as revenueRigettato,  " +
                "       COALESCE(round(CAST((SUM(trans.revenue) - SUM(trans.value)) AS numeric), 2), 0)                           as margine,  " +
                "       COALESCE(round(CAST((SUM(trans.revenue) - SUM(trans.value)) AS numeric) / CAST(SUM(NULLIF(trans.revenue, 0)) AS numeric) *  " +
                "                      100, 2), 0)                                                                                as marginePC,  " +
                "       COALESCE(round(CAST(SUM(trans.value) / NULLIF(SUM(trans.impressionnumber), 0) * 1000 AS numeric), 2), 0)             as ecpm,  " +
                "       COALESCE(round(CAST(SUM(trans.value) / NULLIF(SUM(trans.clicknumber), 0) AS numeric), 2), 0)              as ecpc,  " +
                "       COALESCE(round(CAST(SUM(trans.value) / NULLIF(SUM(trans.leadnumber), 0) AS numeric), 2), 0)               as ecpl  " +
                "from trans  " +
                "group by trans.datetime  " +
                "order by giorno;",
        resultSetMapping = "Mapping.searchReportDaily")
@SqlResultSetMapping(name = "Mapping.searchReportDaily",
        classes = @ConstructorResult(targetClass = ReportDailyDTO.class,
                columns = {
                        @ColumnResult(name = "giorno", type = String.class),
                        @ColumnResult(name = "commission", type = Double.class),
                        @ColumnResult(name = "commissionRigettato", type = Double.class),
                        @ColumnResult(name = "revenue", type = Double.class),
                        @ColumnResult(name = "revenueRigettato", type = Double.class),
                        @ColumnResult(name = "margine", type = Double.class),
                        @ColumnResult(name = "marginePC", type = Double.class),
                        @ColumnResult(name = "ecpm", type = String.class),
                        @ColumnResult(name = "ecpc", type = String.class),
                        @ColumnResult(name = "ecpl", type = String.class),
                        @ColumnResult(name = "impressionNumber", type = Long.class),
                        @ColumnResult(name = "clickNumber", type = Long.class),
                        @ColumnResult(name = "leadNumber", type = Long.class),
                        @ColumnResult(name = "ctr", type = String.class),
                        @ColumnResult(name = "lr", type = String.class),
                        @ColumnResult(name = "leadNumberRigettato", type = Long.class),
                        @ColumnResult(name = "clickNumberRigettato", type = Long.class),
                }))

@NamedNativeQuery(name = "Report.searchReportCampaign",
        query = "WITH transazioni AS (SELECT row_number() OVER ()                                                     AS rn, " +
                "                            CAST('CPC' as text)                                                      AS tipo, " +
                "                            CAST(t.id as bigint)                                                     AS id, " +
                "                            t.creation_date                                                          AS creationdate, " +
                "                            date(t.date_time)                                                        AS datetime, " +
                "                            CAST(t.status_id as bigint)                                              AS statusid, " +
                "                            tdd.name                                                                 AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
                "                            td.name                                                                  AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
                "                            ta.name                                                                  AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                  AS advertiserid, " +
                "                            tadv.name                                                                AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                             AS channelid, " +
                "                            c.name                                                                   AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                            AS campaignid, " +
                "                            tc.name                                                                  AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                               AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                          AS commissionid, " +
                "                            tco.name                                                                 AS commissionname, " +
                "                            tco.value                                                                AS commissionvalue, " +
                "                            CAST(0 as numeric)                                                       AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                       AS value, " +
                "                            CAST(0 as numeric)                                                       AS valuerigettato, " +
                "                            CAST(t.revenue_id as bigint)                                             AS revenueid, " +
                "                            trf.revenue                                                              AS revenuevalue, " +
                "                            CAST(0 as numeric)                                                       AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue, " +
                "                            CAST(0 as numeric)                                                       AS revenuerigettato, " +
                "                            t.click_number                                                           AS clicknumber, " +
                "                            CAST(0 as bigint)                                                        AS clicknumberrigettato, " +
                "                            CAST(0 as bigint)                                                        AS impressionnumber, " +
                "                            CAST(0 as bigint)                                                        AS leadnumber, " +
                "                            CAST(0 as bigint)                                                        AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                          AS data, " +
                "                            CAST(t.wallet_id as bigint)                                              AS walletid, " +
                "                            t.payout_present                                                         AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                              AS payoutid, " +
                "                            t.payout_reference                                                       AS payoutreference " +
                "                     FROM t_transaction_cpc as t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 72 " +
                "                         OR t.status_id = 73) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                     AS rn, " +
                "                            CAST('CPC' AS text)                                                      AS tipo, " +
                "                            CAST(t.id as bigint)                                                     AS id, " +
                "                            t.creation_date                                                          AS creationdate, " +
                "                            date(t.date_time)                                                        AS datetime, " +
                "                            CAST(t.status_id as bigint)                                              AS statusid, " +
                "                            tdd.name                                                                 AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
                "                            td.name                                                                  AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
                "                            ta.name                                                                  AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                  AS advertiserid, " +
                "                            tadv.name                                                                AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                             AS channelid, " +
                "                            c.name                                                                   AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                            AS campaignid, " +
                "                            tc.name                                                                  AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                               AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                          AS commissionid, " +
                "                            tco.name                                                                 AS commissionname, " +
                "                            0                                                                        AS commissionvalue, " +
                "                            tco.value                                                                AS commissionvaluerigettato, " +
                "                            0                                                                        AS value, " +
                "                            round(CAST(t.value AS numeric), 2)                                       AS valuerigettato, " +
                "                            t.revenue_id                                                             AS revenueid, " +
                "                            0                                                                        AS revenuevalue, " +
                "                            trf.revenue                                                              AS revenuevaluerigettato, " +
                "                            0                                                                        AS revenue, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenuerigettato, " +
                "                            0                                                                        AS clicknumber, " +
                "                            t.click_number                                                           AS clicknumberrigettato, " +
                "                            0                                                                        AS impressionnumber, " +
                "                            0                                                                        AS leadnumber, " +
                "                            0                                                                        AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                          AS data, " +
                "                            CAST(t.wallet_id as bigint)                                              AS walletid, " +
                "                            t.payout_present                                                         AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                              AS payoutid, " +
                "                            t.payout_reference                                                       AS payoutreference " +
                "                     FROM t_transaction_cpc t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 74 " +
                "                         OR t.status_id = 70) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                    AS rn, " +
                "                            CAST('CPL' AS text)                                                     AS tipo, " +
                "                            CAST(t.id as bigint)                                                    AS id, " +
                "                            t.creation_date                                                         AS creationdate, " +
                "                            date(t.date_time)                                                       AS datetime, " +
                "                            CAST(t.status_id as bigint)                                             AS statusid, " +
                "                            tdd.name                                                                AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                "                            td.name                                                                 AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                "                            ta.name                                                                 AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                "                            tadv.name                                                               AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                            AS channelid, " +
                "                            c.name                                                                  AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                "                            tc.name                                                                 AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                              AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                         AS commissionid, " +
                "                            tco.name                                                                AS commissionname, " +
                "                            tco.value                                                               AS commissionvalue, " +
                "                            0                                                                       AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                      AS value, " +
                "                            0                                                                       AS valuerigettato, " +
                "                            t.revenue_id                                                            AS revenueid, " +
                "                            trf.revenue                                                             AS revenuevalue, " +
                "                            0                                                                       AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue, " +
                "                            0                                                                       AS revenuerigettato, " +
                "                            0                                                                       AS clicknumber, " +
                "                            0                                                                       AS clicknumberrigettato, " +
                "                            0                                                                       AS impressionnumber, " +
                "                            1                                                                       AS leadnumber, " +
                "                            0                                                                       AS leadnumberrigettato, " +
                "                            t.data                                                                  AS data, " +
                "                            CAST(t.wallet_id as bigint)                                             AS walletid, " +
                "                            t.payout_present                                                        AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                             AS payoutid, " +
                "                            t.payout_reference                                                      AS payoutreference " +
                "                     FROM t_transaction_cpl t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 72 " +
                "                         OR t.status_id = 73) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                    AS rn, " +
                "                            CAST('CPL' AS text)                                                     AS tipo, " +
                "                            CAST(t.id as bigint)                                                    AS id, " +
                "                            t.creation_date                                                         AS creationdate, " +
                "                            date(t.date_time)                                                       AS datetime, " +
                "                            CAST(t.status_id as bigint)                                             AS statusid, " +
                "                            tdd.name                                                                AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                "                            td.name                                                                 AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                "                            ta.name                                                                 AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                "                            tadv.name                                                               AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                            AS channelid, " +
                "                            c.name                                                                  AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                "                            tc.name                                                                 AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                              AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                         AS commissionid, " +
                "                            tco.name                                                                AS commissionname, " +
                "                            0                                                                       AS commissionvalue, " +
                "                            tco.value                                                               AS commissionvaluerigettato, " +
                "                            0                                                                       AS value, " +
                "                            round(CAST(t.value AS numeric), 2)                                      AS valuerigettato, " +
                "                            t.revenue_id                                                            AS revenueid, " +
                "                            0                                                                       AS revenuevalue, " +
                "                            trf.revenue                                                             AS revenuevaluerigettato, " +
                "                            0                                                                       AS revenue, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenuerigettato, " +
                "                            0                                                                       AS clicknumber, " +
                "                            0                                                                       AS clicknumberrigettato, " +
                "                            0                                                                       AS impressionnumber, " +
                "                            0                                                                       AS leadnumber, " +
                "                            1                                                                       AS leadnumberrigettato, " +
                "                            t.data                                                                  AS data, " +
                "                            CAST(t.wallet_id as bigint)                                             AS walletid, " +
                "                            t.payout_present                                                        AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                             AS payoutid, " +
                "                            t.payout_reference                                                      AS payoutreference " +
                "                     FROM t_transaction_cpl t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 74 " +
                "                         OR t.status_id = 70) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                          AS rn, " +
                "                            CAST('CPM' AS text)                                                           AS tipo, " +
                "                            CAST(t.id as bigint)                                                          AS id, " +
                "                            t.creation_date                                                               AS creationdate, " +
                "                            date(t.date_time)                                                             AS datetime, " +
                "                            CAST(t.status_id as bigint)                                                   AS statusid, " +
                "                            tdd.name                                                                      AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                               AS dictionaryid, " +
                "                            td.name                                                                       AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                                AS affiliateid, " +
                "                            ta.name                                                                       AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                       AS advertiserid, " +
                "                            tadv.name                                                                     AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                                  AS channelid, " +
                "                            c.name                                                                        AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                                 AS campaignid, " +
                "                            tc.name                                                                       AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                                    AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                               AS commissionid, " +
                "                            tco.name                                                                      AS commissionname, " +
                "                            tco.value                                                                     AS commissionvalue, " +
                "                            0                                                                             AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                            AS value, " +
                "                            0                                                                             AS valuerigettato, " +
                "                            t.revenue_id                                                                  AS revenueid, " +
                "                            trf.revenue                                                                   AS revenuevalue, " +
                "                            0                                                                             AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.impression_number AS numeric), 2) AS revenue, " +
                "                            0                                                                             AS revenuerigettato, " +
                "                            0                                                                             AS clicknumber, " +
                "                            0                                                                             AS clicknumberrigettato, " +
                "                            t.impression_number                                                           AS impressionnumber, " +
                "                            0                                                                             AS leadnumber, " +
                "                            0                                                                             AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                               AS data, " +
                "                            CAST(t.wallet_id as bigint)                                                   AS walletid, " +
                "                            t.payout_present                                                              AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                                   AS payoutid, " +
                "                            t.payout_reference                                                            AS payoutreference " +
                "                     FROM t_transaction_cpm t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId)))) " +
                "SELECT transazioni.campaignid                                                                                                as campaignid, " +
                "       transazioni.campaignname                                                                                              as campaignname, " +
                "       COALESCE(SUM(transazioni.impressionnumber), 0)                                                                        as impressionNumber, " +
                "       COALESCE(SUM(transazioni.clicknumber), 0)                                                                             as clickNumber, " +
                "       COALESCE(SUM(transazioni.clicknumberrigettato), 0)                                                                    as clickNumberRigettato, " +
                "       COALESCE(SUM(transazioni.leadnumber), 0)                                                                              as leadNumber, " +
                "       COALESCE(SUM(transazioni.leadnumberrigettato), 0)                                                                     as leadNumberRigettato, " +
                "       COALESCE(round(SUM(transazioni.clicknumber) / NULLIF(SUM(transazioni.impressionnumber), 0) * 100, 2), 0)              as CTR, " +
                "       COALESCE(round(CAST((SUM(transazioni.leadnumber) / NULLIF(SUM(transazioni.clicknumber), 0) * 100) AS numeric), 2), 0) as LR, " +
                "       COALESCE(round(CAST((SUM(transazioni.leadnumber) / NULLIF(SUM(transazioni.clicknumber), 0) * 100) AS numeric), 2), 0) as LR, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) AS numeric), 2), 0)                                                        as commission, " +
                "       COALESCE(round(CAST(SUM(transazioni.valuerigettato) AS numeric), 2), 0)                                               as commissionRigettato, " +
                "       COALESCE(round(CAST(SUM(transazioni.revenue) AS numeric), 2), 0)                                                      as revenue, " +
                "       COALESCE(round(CAST(SUM(transazioni.revenuerigettato) AS numeric), 2), 0)                                             as revenueRigettato, " +
                "       COALESCE(round(CAST((SUM(transazioni.revenue) - SUM(transazioni.value)) AS numeric), 2), 0)                           as margine, " +
                "       COALESCE(round(CAST((SUM(transazioni.revenue) - SUM(transazioni.value)) AS numeric) / CAST(SUM(NULLIF(transazioni.revenue, 0)) AS numeric) * " +
                "                      100, 2), 0)                                                                                            as marginePC, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.impressionnumber), 0) * 1000 AS numeric), 2), 0)  as ecpm, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.clicknumber), 0) AS numeric), 2), 0)              as ecpc, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.leadnumber), 0) AS numeric), 2), 0)               as ecpl, " +
                "       0                                                                                      as initialBudget, " +
                "       0                                                                 as budget, " +
                "       0                                                                                                           as budgetGivenPC, " +
                "       0                                                                                                           as budgetPC " +
                " FROM transazioni " +
                " GROUP BY transazioni.campaignid, transazioni.campaignname " +
                " ORDER BY transazioni.campaignname ASC;",
        resultSetMapping = "Mapping.searchReportCampaign")
@SqlResultSetMapping(name = "Mapping.searchReportCampaign",
        classes = @ConstructorResult(targetClass = ReportCampagneDTO.class,
                columns = {
                        @ColumnResult(name = "commission", type = Double.class),
                        @ColumnResult(name = "commissionRigettato", type = Double.class),
                        @ColumnResult(name = "revenue", type = Double.class),
                        @ColumnResult(name = "revenueRigettato", type = Double.class),
                        @ColumnResult(name = "margine", type = Double.class),
                        @ColumnResult(name = "marginePC", type = Double.class),
                        @ColumnResult(name = "ecpm", type = String.class),
                        @ColumnResult(name = "ecpc", type = String.class),
                        @ColumnResult(name = "ecpl", type = String.class),
                        @ColumnResult(name = "impressionNumber", type = Long.class),
                        @ColumnResult(name = "clickNumber", type = Long.class),
                        @ColumnResult(name = "leadNumber", type = Long.class),
                        @ColumnResult(name = "ctr", type = String.class),
                        @ColumnResult(name = "lr", type = String.class),
                        @ColumnResult(name = "leadNumberRigettato", type = Long.class),
                        @ColumnResult(name = "clickNumberRigettato", type = Long.class),
                        @ColumnResult(name = "initialBudget", type = Double.class),
                        @ColumnResult(name = "budget", type = Double.class),
                        @ColumnResult(name = "budgetGivenPC", type = Double.class),
                        @ColumnResult(name = "budgetPC", type = Double.class),
                        @ColumnResult(name = "campaignId", type = Long.class),
                        @ColumnResult(name = "campaignName", type = String.class)
                }))

@NamedNativeQuery(name = "Report.searchReportAffiliate",
        query = "WITH transazioni AS (SELECT row_number() OVER ()                                                     AS rn, " +
                "                            CAST('CPC' as text)                                                      AS tipo, " +
                "                            CAST(t.id as bigint)                                                     AS id, " +
                "                            t.creation_date                                                          AS creationdate, " +
                "                            date(t.date_time)                                                        AS datetime, " +
                "                            CAST(t.status_id as bigint)                                              AS statusid, " +
                "                            tdd.name                                                                 AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
                "                            td.name                                                                  AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
                "                            ta.name                                                                  AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                  AS advertiserid, " +
                "                            tadv.name                                                                AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                             AS channelid, " +
                "                            c.name                                                                   AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                            AS campaignid, " +
                "                            tc.name                                                                  AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                               AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                          AS commissionid, " +
                "                            tco.name                                                                 AS commissionname, " +
                "                            tco.value                                                                AS commissionvalue, " +
                "                            CAST(0 as numeric)                                                       AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                       AS value, " +
                "                            CAST(0 as numeric)                                                       AS valuerigettato, " +
                "                            CAST(t.revenue_id as bigint)                                             AS revenueid, " +
                "                            trf.revenue                                                              AS revenuevalue, " +
                "                            CAST(0 as numeric)                                                       AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue, " +
                "                            CAST(0 as numeric)                                                       AS revenuerigettato, " +
                "                            t.click_number                                                           AS clicknumber, " +
                "                            CAST(0 as bigint)                                                        AS clicknumberrigettato, " +
                "                            CAST(0 as bigint)                                                        AS impressionnumber, " +
                "                            CAST(0 as bigint)                                                        AS leadnumber, " +
                "                            CAST(0 as bigint)                                                        AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                          AS data, " +
                "                            CAST(t.wallet_id as bigint)                                              AS walletid, " +
                "                            t.payout_present                                                         AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                              AS payoutid, " +
                "                            t.payout_reference                                                       AS payoutreference " +
                "                     FROM t_transaction_cpc as t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 72 " +
                "                         OR t.status_id = 73) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                     AS rn, " +
                "                            CAST('CPC' AS text)                                                      AS tipo, " +
                "                            CAST(t.id as bigint)                                                     AS id, " +
                "                            t.creation_date                                                          AS creationdate, " +
                "                            date(t.date_time)                                                        AS datetime, " +
                "                            CAST(t.status_id as bigint)                                              AS statusid, " +
                "                            tdd.name                                                                 AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
                "                            td.name                                                                  AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
                "                            ta.name                                                                  AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                  AS advertiserid, " +
                "                            tadv.name                                                                AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                             AS channelid, " +
                "                            c.name                                                                   AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                            AS campaignid, " +
                "                            tc.name                                                                  AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                               AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                          AS commissionid, " +
                "                            tco.name                                                                 AS commissionname, " +
                "                            0                                                                        AS commissionvalue, " +
                "                            tco.value                                                                AS commissionvaluerigettato, " +
                "                            0                                                                        AS value, " +
                "                            round(CAST(t.value AS numeric), 2)                                       AS valuerigettato, " +
                "                            t.revenue_id                                                             AS revenueid, " +
                "                            0                                                                        AS revenuevalue, " +
                "                            trf.revenue                                                              AS revenuevaluerigettato, " +
                "                            0                                                                        AS revenue, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenuerigettato, " +
                "                            0                                                                        AS clicknumber, " +
                "                            t.click_number                                                           AS clicknumberrigettato, " +
                "                            0                                                                        AS impressionnumber, " +
                "                            0                                                                        AS leadnumber, " +
                "                            0                                                                        AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                          AS data, " +
                "                            CAST(t.wallet_id as bigint)                                              AS walletid, " +
                "                            t.payout_present                                                         AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                              AS payoutid, " +
                "                            t.payout_reference                                                       AS payoutreference " +
                "                     FROM t_transaction_cpc t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 74 " +
                "                         OR t.status_id = 70) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                    AS rn, " +
                "                            CAST('CPL' AS text)                                                     AS tipo, " +
                "                            CAST(t.id as bigint)                                                    AS id, " +
                "                            t.creation_date                                                         AS creationdate, " +
                "                            date(t.date_time)                                                       AS datetime, " +
                "                            CAST(t.status_id as bigint)                                             AS statusid, " +
                "                            tdd.name                                                                AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                "                            td.name                                                                 AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                "                            ta.name                                                                 AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                "                            tadv.name                                                               AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                            AS channelid, " +
                "                            c.name                                                                  AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                "                            tc.name                                                                 AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                              AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                         AS commissionid, " +
                "                            tco.name                                                                AS commissionname, " +
                "                            tco.value                                                               AS commissionvalue, " +
                "                            0                                                                       AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                      AS value, " +
                "                            0                                                                       AS valuerigettato, " +
                "                            t.revenue_id                                                            AS revenueid, " +
                "                            trf.revenue                                                             AS revenuevalue, " +
                "                            0                                                                       AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue, " +
                "                            0                                                                       AS revenuerigettato, " +
                "                            0                                                                       AS clicknumber, " +
                "                            0                                                                       AS clicknumberrigettato, " +
                "                            0                                                                       AS impressionnumber, " +
                "                            1                                                                       AS leadnumber, " +
                "                            0                                                                       AS leadnumberrigettato, " +
                "                            t.data                                                                  AS data, " +
                "                            CAST(t.wallet_id as bigint)                                             AS walletid, " +
                "                            t.payout_present                                                        AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                             AS payoutid, " +
                "                            t.payout_reference                                                      AS payoutreference " +
                "                     FROM t_transaction_cpl t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 72 " +
                "                         OR t.status_id = 73) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                    AS rn, " +
                "                            CAST('CPL' AS text)                                                     AS tipo, " +
                "                            CAST(t.id as bigint)                                                    AS id, " +
                "                            t.creation_date                                                         AS creationdate, " +
                "                            date(t.date_time)                                                       AS datetime, " +
                "                            CAST(t.status_id as bigint)                                             AS statusid, " +
                "                            tdd.name                                                                AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                "                            td.name                                                                 AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                "                            ta.name                                                                 AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                "                            tadv.name                                                               AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                            AS channelid, " +
                "                            c.name                                                                  AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                "                            tc.name                                                                 AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                              AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                         AS commissionid, " +
                "                            tco.name                                                                AS commissionname, " +
                "                            0                                                                       AS commissionvalue, " +
                "                            tco.value                                                               AS commissionvaluerigettato, " +
                "                            0                                                                       AS value, " +
                "                            round(CAST(t.value AS numeric), 2)                                      AS valuerigettato, " +
                "                            t.revenue_id                                                            AS revenueid, " +
                "                            0                                                                       AS revenuevalue, " +
                "                            trf.revenue                                                             AS revenuevaluerigettato, " +
                "                            0                                                                       AS revenue, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenuerigettato, " +
                "                            0                                                                       AS clicknumber, " +
                "                            0                                                                       AS clicknumberrigettato, " +
                "                            0                                                                       AS impressionnumber, " +
                "                            0                                                                       AS leadnumber, " +
                "                            1                                                                       AS leadnumberrigettato, " +
                "                            t.data                                                                  AS data, " +
                "                            CAST(t.wallet_id as bigint)                                             AS walletid, " +
                "                            t.payout_present                                                        AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                             AS payoutid, " +
                "                            t.payout_reference                                                      AS payoutreference " +
                "                     FROM t_transaction_cpl t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 74 " +
                "                         OR t.status_id = 70) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                          AS rn, " +
                "                            CAST('CPM' AS text)                                                           AS tipo, " +
                "                            CAST(t.id as bigint)                                                          AS id, " +
                "                            t.creation_date                                                               AS creationdate, " +
                "                            date(t.date_time)                                                             AS datetime, " +
                "                            CAST(t.status_id as bigint)                                                   AS statusid, " +
                "                            tdd.name                                                                      AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                               AS dictionaryid, " +
                "                            td.name                                                                       AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                                AS affiliateid, " +
                "                            ta.name                                                                       AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                       AS advertiserid, " +
                "                            tadv.name                                                                     AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                                  AS channelid, " +
                "                            c.name                                                                        AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                                 AS campaignid, " +
                "                            tc.name                                                                       AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                                    AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                               AS commissionid, " +
                "                            tco.name                                                                      AS commissionname, " +
                "                            tco.value                                                                     AS commissionvalue, " +
                "                            0                                                                             AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                            AS value, " +
                "                            0                                                                             AS valuerigettato, " +
                "                            t.revenue_id                                                                  AS revenueid, " +
                "                            trf.revenue                                                                   AS revenuevalue, " +
                "                            0                                                                             AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.impression_number AS numeric), 2) AS revenue, " +
                "                            0                                                                             AS revenuerigettato, " +
                "                            0                                                                             AS clicknumber, " +
                "                            0                                                                             AS clicknumberrigettato, " +
                "                            t.impression_number                                                           AS impressionnumber, " +
                "                            0                                                                             AS leadnumber, " +
                "                            0                                                                             AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                               AS data, " +
                "                            CAST(t.wallet_id as bigint)                                                   AS walletid, " +
                "                            t.payout_present                                                              AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                                   AS payoutid, " +
                "                            t.payout_reference                                                            AS payoutreference " +
                "                     FROM t_transaction_cpm t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId)))) " +
                "SELECT transazioni.affiliateid                                                                                               as affiliateid, " +
                "       transazioni.affiliatename                                                                                             as affiliatename, " +
                "       COALESCE(SUM(transazioni.impressionnumber), 0)                                                                        as impressionNumber, " +
                "       COALESCE(SUM(transazioni.clicknumber), 0)                                                                             as clickNumber, " +
                "       COALESCE(SUM(transazioni.clicknumberrigettato), 0)                                                                    as clickNumberRigettato, " +
                "       COALESCE(SUM(transazioni.leadnumber), 0)                                                                              as leadNumber, " +
                "       COALESCE(SUM(transazioni.leadnumberrigettato), 0)                                                                     as leadNumberRigettato, " +
                "       COALESCE(round(SUM(transazioni.clicknumber) / NULLIF(SUM(transazioni.impressionnumber), 0) * 100, 2), 0)              as CTR, " +
                "       COALESCE(round(CAST((SUM(transazioni.leadnumber) / NULLIF(SUM(transazioni.clicknumber), 0) * 100) AS numeric), 2), 0) as LR, " +
                "       COALESCE(round(CAST((SUM(transazioni.leadnumber) / NULLIF(SUM(transazioni.clicknumber), 0) * 100) AS numeric), 2), 0) as LR, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) AS numeric), 2), 0)                                                        as commission, " +
                "       COALESCE(round(CAST(SUM(transazioni.valuerigettato) AS numeric), 2), 0)                                               as commissionRigettato, " +
                "       COALESCE(round(CAST(SUM(transazioni.revenue) AS numeric), 2), 0)                                                      as revenue, " +
                "       COALESCE(round(CAST(SUM(transazioni.revenuerigettato) AS numeric), 2), 0)                                             as revenueRigettato, " +
                "       COALESCE(round(CAST((SUM(transazioni.revenue) - SUM(transazioni.value)) AS numeric), 2), 0)                           as margine, " +
                "       COALESCE(round(CAST((SUM(transazioni.revenue) - SUM(transazioni.value)) AS numeric) / CAST(SUM(NULLIF(transazioni.revenue, 0)) AS numeric) * " +
                "                      100, 2), 0)                                                                                            as marginePC, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.impressionnumber), 0) * 1000 AS numeric), 2), 0)  as ecpm, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.clicknumber), 0) AS numeric), 2), 0)              as ecpc, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.leadnumber), 0) AS numeric), 2), 0)               as ecpl " +
                " " +
                "FROM transazioni " +
                "GROUP BY transazioni.affiliateid, transazioni.affiliatename " +
                "ORDER BY transazioni.affiliatename ASC;  ",
        resultSetMapping = "Mapping.searchReportAffiliate")
@SqlResultSetMapping(name = "Mapping.searchReportAffiliate",
        classes = @ConstructorResult(targetClass = ReportAffiliatesDTO.class,
                columns = {
                        @ColumnResult(name = "affiliateId", type = Long.class),
                        @ColumnResult(name = "affiliateName", type = String.class),
                        @ColumnResult(name = "commission", type = Double.class),
                        @ColumnResult(name = "commissionRigettato", type = Double.class),
                        @ColumnResult(name = "revenue", type = Double.class),
                        @ColumnResult(name = "revenueRigettato", type = Double.class),
                        @ColumnResult(name = "margine", type = Double.class),
                        @ColumnResult(name = "marginePC", type = Double.class),
                        @ColumnResult(name = "ecpm", type = String.class),
                        @ColumnResult(name = "ecpc", type = String.class),
                        @ColumnResult(name = "ecpl", type = String.class),
                        @ColumnResult(name = "impressionNumber", type = Long.class),
                        @ColumnResult(name = "clickNumber", type = Long.class),
                        @ColumnResult(name = "leadNumber", type = Long.class),
                        @ColumnResult(name = "ctr", type = String.class),
                        @ColumnResult(name = "lr", type = String.class),
                        @ColumnResult(name = "leadNumberRigettato", type = Long.class),
                        @ColumnResult(name = "clickNumberRigettato", type = Long.class),
                }))


@NamedNativeQuery(name = "Report.searchReportAffiliateChannel",
        query = "WITH transazioni AS (SELECT row_number() OVER ()                                                     AS rn, " +
                "                            CAST('CPC' as text)                                                      AS tipo, " +
                "                            CAST(t.id as bigint)                                                     AS id, " +
                "                            t.creation_date                                                          AS creationdate, " +
                "                            date(t.date_time)                                                        AS datetime, " +
                "                            CAST(t.status_id as bigint)                                              AS statusid, " +
                "                            tdd.name                                                                 AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
                "                            td.name                                                                  AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
                "                            ta.name                                                                  AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                  AS advertiserid, " +
                "                            tadv.name                                                                AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                             AS channelid, " +
                "                            c.name                                                                   AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                            AS campaignid, " +
                "                            tc.name                                                                  AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                               AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                          AS commissionid, " +
                "                            tco.name                                                                 AS commissionname, " +
                "                            tco.value                                                                AS commissionvalue, " +
                "                            CAST(0 as numeric)                                                       AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                       AS value, " +
                "                            CAST(0 as numeric)                                                       AS valuerigettato, " +
                "                            CAST(t.revenue_id as bigint)                                             AS revenueid, " +
                "                            trf.revenue                                                              AS revenuevalue, " +
                "                            CAST(0 as numeric)                                                       AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue, " +
                "                            CAST(0 as numeric)                                                       AS revenuerigettato, " +
                "                            t.click_number                                                           AS clicknumber, " +
                "                            CAST(0 as bigint)                                                        AS clicknumberrigettato, " +
                "                            CAST(0 as bigint)                                                        AS impressionnumber, " +
                "                            CAST(0 as bigint)                                                        AS leadnumber, " +
                "                            CAST(0 as bigint)                                                        AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                          AS data, " +
                "                            CAST(t.wallet_id as bigint)                                              AS walletid, " +
                "                            t.payout_present                                                         AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                              AS payoutid, " +
                "                            t.payout_reference                                                       AS payoutreference " +
                "                     FROM t_transaction_cpc as t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 72 " +
                "                         OR t.status_id = 73) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                     AS rn, " +
                "                            CAST('CPC' AS text)                                                      AS tipo, " +
                "                            CAST(t.id as bigint)                                                     AS id, " +
                "                            t.creation_date                                                          AS creationdate, " +
                "                            date(t.date_time)                                                        AS datetime, " +
                "                            CAST(t.status_id as bigint)                                              AS statusid, " +
                "                            tdd.name                                                                 AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
                "                            td.name                                                                  AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
                "                            ta.name                                                                  AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                  AS advertiserid, " +
                "                            tadv.name                                                                AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                             AS channelid, " +
                "                            c.name                                                                   AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                            AS campaignid, " +
                "                            tc.name                                                                  AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                               AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                          AS commissionid, " +
                "                            tco.name                                                                 AS commissionname, " +
                "                            0                                                                        AS commissionvalue, " +
                "                            tco.value                                                                AS commissionvaluerigettato, " +
                "                            0                                                                        AS value, " +
                "                            round(CAST(t.value AS numeric), 2)                                       AS valuerigettato, " +
                "                            t.revenue_id                                                             AS revenueid, " +
                "                            0                                                                        AS revenuevalue, " +
                "                            trf.revenue                                                              AS revenuevaluerigettato, " +
                "                            0                                                                        AS revenue, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenuerigettato, " +
                "                            0                                                                        AS clicknumber, " +
                "                            t.click_number                                                           AS clicknumberrigettato, " +
                "                            0                                                                        AS impressionnumber, " +
                "                            0                                                                        AS leadnumber, " +
                "                            0                                                                        AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                          AS data, " +
                "                            CAST(t.wallet_id as bigint)                                              AS walletid, " +
                "                            t.payout_present                                                         AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                              AS payoutid, " +
                "                            t.payout_reference                                                       AS payoutreference " +
                "                     FROM t_transaction_cpc t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 74 " +
                "                         OR t.status_id = 70) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                    AS rn, " +
                "                            CAST('CPL' AS text)                                                     AS tipo, " +
                "                            CAST(t.id as bigint)                                                    AS id, " +
                "                            t.creation_date                                                         AS creationdate, " +
                "                            date(t.date_time)                                                       AS datetime, " +
                "                            CAST(t.status_id as bigint)                                             AS statusid, " +
                "                            tdd.name                                                                AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                "                            td.name                                                                 AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                "                            ta.name                                                                 AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                "                            tadv.name                                                               AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                            AS channelid, " +
                "                            c.name                                                                  AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                "                            tc.name                                                                 AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                              AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                         AS commissionid, " +
                "                            tco.name                                                                AS commissionname, " +
                "                            tco.value                                                               AS commissionvalue, " +
                "                            0                                                                       AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                      AS value, " +
                "                            0                                                                       AS valuerigettato, " +
                "                            t.revenue_id                                                            AS revenueid, " +
                "                            trf.revenue                                                             AS revenuevalue, " +
                "                            0                                                                       AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue, " +
                "                            0                                                                       AS revenuerigettato, " +
                "                            0                                                                       AS clicknumber, " +
                "                            0                                                                       AS clicknumberrigettato, " +
                "                            0                                                                       AS impressionnumber, " +
                "                            1                                                                       AS leadnumber, " +
                "                            0                                                                       AS leadnumberrigettato, " +
                "                            t.data                                                                  AS data, " +
                "                            CAST(t.wallet_id as bigint)                                             AS walletid, " +
                "                            t.payout_present                                                        AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                             AS payoutid, " +
                "                            t.payout_reference                                                      AS payoutreference " +
                "                     FROM t_transaction_cpl t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 72 " +
                "                         OR t.status_id = 73) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                    AS rn, " +
                "                            CAST('CPL' AS text)                                                     AS tipo, " +
                "                            CAST(t.id as bigint)                                                    AS id, " +
                "                            t.creation_date                                                         AS creationdate, " +
                "                            date(t.date_time)                                                       AS datetime, " +
                "                            CAST(t.status_id as bigint)                                             AS statusid, " +
                "                            tdd.name                                                                AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                "                            td.name                                                                 AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                "                            ta.name                                                                 AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                "                            tadv.name                                                               AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                            AS channelid, " +
                "                            c.name                                                                  AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                "                            tc.name                                                                 AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                              AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                         AS commissionid, " +
                "                            tco.name                                                                AS commissionname, " +
                "                            0                                                                       AS commissionvalue, " +
                "                            tco.value                                                               AS commissionvaluerigettato, " +
                "                            0                                                                       AS value, " +
                "                            round(CAST(t.value AS numeric), 2)                                      AS valuerigettato, " +
                "                            t.revenue_id                                                            AS revenueid, " +
                "                            0                                                                       AS revenuevalue, " +
                "                            trf.revenue                                                             AS revenuevaluerigettato, " +
                "                            0                                                                       AS revenue, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenuerigettato, " +
                "                            0                                                                       AS clicknumber, " +
                "                            0                                                                       AS clicknumberrigettato, " +
                "                            0                                                                       AS impressionnumber, " +
                "                            0                                                                       AS leadnumber, " +
                "                            1                                                                       AS leadnumberrigettato, " +
                "                            t.data                                                                  AS data, " +
                "                            CAST(t.wallet_id as bigint)                                             AS walletid, " +
                "                            t.payout_present                                                        AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                             AS payoutid, " +
                "                            t.payout_reference                                                      AS payoutreference " +
                "                     FROM t_transaction_cpl t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 74 " +
                "                         OR t.status_id = 70) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                          AS rn, " +
                "                            CAST('CPM' AS text)                                                           AS tipo, " +
                "                            CAST(t.id as bigint)                                                          AS id, " +
                "                            t.creation_date                                                               AS creationdate, " +
                "                            date(t.date_time)                                                             AS datetime, " +
                "                            CAST(t.status_id as bigint)                                                   AS statusid, " +
                "                            tdd.name                                                                      AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                               AS dictionaryid, " +
                "                            td.name                                                                       AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                                AS affiliateid, " +
                "                            ta.name                                                                       AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                       AS advertiserid, " +
                "                            tadv.name                                                                     AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                                  AS channelid, " +
                "                            c.name                                                                        AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                                 AS campaignid, " +
                "                            tc.name                                                                       AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                                    AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                               AS commissionid, " +
                "                            tco.name                                                                      AS commissionname, " +
                "                            tco.value                                                                     AS commissionvalue, " +
                "                            0                                                                             AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                            AS value, " +
                "                            0                                                                             AS valuerigettato, " +
                "                            t.revenue_id                                                                  AS revenueid, " +
                "                            trf.revenue                                                                   AS revenuevalue, " +
                "                            0                                                                             AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.impression_number AS numeric), 2) AS revenue, " +
                "                            0                                                                             AS revenuerigettato, " +
                "                            0                                                                             AS clicknumber, " +
                "                            0                                                                             AS clicknumberrigettato, " +
                "                            t.impression_number                                                           AS impressionnumber, " +
                "                            0                                                                             AS leadnumber, " +
                "                            0                                                                             AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                               AS data, " +
                "                            CAST(t.wallet_id as bigint)                                                   AS walletid, " +
                "                            t.payout_present                                                              AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                                   AS payoutid, " +
                "                            t.payout_reference                                                            AS payoutreference " +
                "                     FROM t_transaction_cpm t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId)))) " +
                "SELECT transazioni.affiliateid                                                                                               as affiliateid, " +
                "       transazioni.affiliatename                                                                                             as affiliatename, " +
                "       transazioni.channelid                                                                                                 as channelid, " +
                "       transazioni.channelname                                                                                               as channelname, " +
                "       COALESCE(SUM(transazioni.impressionnumber), 0)                                                                        as impressionNumber, " +
                "       COALESCE(SUM(transazioni.clicknumber), 0)                                                                             as clickNumber, " +
                "       COALESCE(SUM(transazioni.clicknumberrigettato), 0)                                                                    as clickNumberRigettato, " +
                "       COALESCE(SUM(transazioni.leadnumber), 0)                                                                              as leadNumber, " +
                "       COALESCE(SUM(transazioni.leadnumberrigettato), 0)                                                                     as leadNumberRigettato, " +
                "       COALESCE(round(SUM(transazioni.clicknumber) / NULLIF(SUM(transazioni.impressionnumber), 0) * 100, 2), 0)              as CTR, " +
                "       COALESCE(round(CAST((SUM(transazioni.leadnumber) / NULLIF(SUM(transazioni.clicknumber), 0) * 100) AS numeric), 2), 0) as LR, " +
                "       COALESCE(round(CAST((SUM(transazioni.leadnumber) / NULLIF(SUM(transazioni.clicknumber), 0) * 100) AS numeric), 2), 0) as LR, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) AS numeric), 2), 0)                                                        as commission, " +
                "       COALESCE(round(CAST(SUM(transazioni.valuerigettato) AS numeric), 2), 0)                                               as commissionRigettato, " +
                "       COALESCE(round(CAST(SUM(transazioni.revenue) AS numeric), 2), 0)                                                      as revenue, " +
                "       COALESCE(round(CAST(SUM(transazioni.revenuerigettato) AS numeric), 2), 0)                                             as revenueRigettato, " +
                "       COALESCE(round(CAST((SUM(transazioni.revenue) - SUM(transazioni.value)) AS numeric), 2), 0)                           as margine, " +
                "       COALESCE(round(CAST((SUM(transazioni.revenue) - SUM(transazioni.value)) AS numeric) / CAST(SUM(NULLIF(transazioni.revenue, 0)) AS numeric) * " +
                "                      100, 2), 0)                                                                                            as marginePC, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.impressionnumber), 0) * 1000 AS numeric), 2), 0)  as ecpm, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.clicknumber), 0) AS numeric), 2), 0)              as ecpc, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.leadnumber), 0) AS numeric), 2), 0)               as ecpl " +
                " " +
                "FROM transazioni " +
                "GROUP BY transazioni.affiliateid, transazioni.affiliatename, transazioni.channelname, transazioni.channelid " +
                "ORDER BY transazioni.affiliatename ASC;   ",
        resultSetMapping = "Mapping.searchReportAffiliateChannel")
@SqlResultSetMapping(name = "Mapping.searchReportAffiliateChannel",
        classes = @ConstructorResult(targetClass = ReportAffiliatesChannelDTO.class,
                columns = {
                        @ColumnResult(name = "affiliateId", type = Long.class),
                        @ColumnResult(name = "affiliateName", type = String.class),
                        @ColumnResult(name = "channelId", type = Long.class),
                        @ColumnResult(name = "channelName", type = String.class),
                        @ColumnResult(name = "commission", type = Double.class),
                        @ColumnResult(name = "commissionRigettato", type = Double.class),
                        @ColumnResult(name = "revenue", type = Double.class),
                        @ColumnResult(name = "revenueRigettato", type = Double.class),
                        @ColumnResult(name = "margine", type = Double.class),
                        @ColumnResult(name = "marginePC", type = Double.class),
                        @ColumnResult(name = "ecpm", type = String.class),
                        @ColumnResult(name = "ecpc", type = String.class),
                        @ColumnResult(name = "ecpl", type = String.class),
                        @ColumnResult(name = "impressionNumber", type = Long.class),
                        @ColumnResult(name = "clickNumber", type = Long.class),
                        @ColumnResult(name = "leadNumber", type = Long.class),
                        @ColumnResult(name = "ctr", type = String.class),
                        @ColumnResult(name = "lr", type = String.class),
                        @ColumnResult(name = "leadNumberRigettato", type = Long.class),
                        @ColumnResult(name = "clickNumberRigettato", type = Long.class),
                }))


@NamedNativeQuery(name = "Report.searchReportAffiliateChannelCampaign",
        query = " WITH transazioni AS (SELECT row_number() OVER ()                                                     AS rn, " +
                "                            CAST('CPC' as text)                                                      AS tipo, " +
                "                            CAST(t.id as bigint)                                                     AS id, " +
                "                            t.creation_date                                                          AS creationdate, " +
                "                            date(t.date_time)                                                        AS datetime, " +
                "                            CAST(t.status_id as bigint)                                              AS statusid, " +
                "                            tdd.name                                                                 AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
                "                            td.name                                                                  AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
                "                            ta.name                                                                  AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                  AS advertiserid, " +
                "                            tadv.name                                                                AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                             AS channelid, " +
                "                            c.name                                                                   AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                            AS campaignid, " +
                "                            tc.name                                                                  AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                               AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                          AS commissionid, " +
                "                            tco.name                                                                 AS commissionname, " +
                "                            tco.value                                                                AS commissionvalue, " +
                "                            CAST(0 as numeric)                                                       AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                       AS value, " +
                "                            CAST(0 as numeric)                                                       AS valuerigettato, " +
                "                            CAST(t.revenue_id as bigint)                                             AS revenueid, " +
                "                            trf.revenue                                                              AS revenuevalue, " +
                "                            CAST(0 as numeric)                                                       AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue, " +
                "                            CAST(0 as numeric)                                                       AS revenuerigettato, " +
                "                            t.click_number                                                           AS clicknumber, " +
                "                            CAST(0 as bigint)                                                        AS clicknumberrigettato, " +
                "                            CAST(0 as bigint)                                                        AS impressionnumber, " +
                "                            CAST(0 as bigint)                                                        AS leadnumber, " +
                "                            CAST(0 as bigint)                                                        AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                          AS data, " +
                "                            CAST(t.wallet_id as bigint)                                              AS walletid, " +
                "                            t.payout_present                                                         AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                              AS payoutid, " +
                "                            t.payout_reference                                                       AS payoutreference " +
                "                     FROM t_transaction_cpc as t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 72 " +
                "                         OR t.status_id = 73) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                     AS rn, " +
                "                            CAST('CPC' AS text)                                                      AS tipo, " +
                "                            CAST(t.id as bigint)                                                     AS id, " +
                "                            t.creation_date                                                          AS creationdate, " +
                "                            date(t.date_time)                                                        AS datetime, " +
                "                            CAST(t.status_id as bigint)                                              AS statusid, " +
                "                            tdd.name                                                                 AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
                "                            td.name                                                                  AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
                "                            ta.name                                                                  AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                  AS advertiserid, " +
                "                            tadv.name                                                                AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                             AS channelid, " +
                "                            c.name                                                                   AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                            AS campaignid, " +
                "                            tc.name                                                                  AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                               AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                          AS commissionid, " +
                "                            tco.name                                                                 AS commissionname, " +
                "                            0                                                                        AS commissionvalue, " +
                "                            tco.value                                                                AS commissionvaluerigettato, " +
                "                            0                                                                        AS value, " +
                "                            round(CAST(t.value AS numeric), 2)                                       AS valuerigettato, " +
                "                            t.revenue_id                                                             AS revenueid, " +
                "                            0                                                                        AS revenuevalue, " +
                "                            trf.revenue                                                              AS revenuevaluerigettato, " +
                "                            0                                                                        AS revenue, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenuerigettato, " +
                "                            0                                                                        AS clicknumber, " +
                "                            t.click_number                                                           AS clicknumberrigettato, " +
                "                            0                                                                        AS impressionnumber, " +
                "                            0                                                                        AS leadnumber, " +
                "                            0                                                                        AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                          AS data, " +
                "                            CAST(t.wallet_id as bigint)                                              AS walletid, " +
                "                            t.payout_present                                                         AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                              AS payoutid, " +
                "                            t.payout_reference                                                       AS payoutreference " +
                "                     FROM t_transaction_cpc t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 74 " +
                "                         OR t.status_id = 70) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                    AS rn, " +
                "                            CAST('CPL' AS text)                                                     AS tipo, " +
                "                            CAST(t.id as bigint)                                                    AS id, " +
                "                            t.creation_date                                                         AS creationdate, " +
                "                            date(t.date_time)                                                       AS datetime, " +
                "                            CAST(t.status_id as bigint)                                             AS statusid, " +
                "                            tdd.name                                                                AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                "                            td.name                                                                 AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                "                            ta.name                                                                 AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                "                            tadv.name                                                               AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                            AS channelid, " +
                "                            c.name                                                                  AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                "                            tc.name                                                                 AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                              AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                         AS commissionid, " +
                "                            tco.name                                                                AS commissionname, " +
                "                            tco.value                                                               AS commissionvalue, " +
                "                            0                                                                       AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                      AS value, " +
                "                            0                                                                       AS valuerigettato, " +
                "                            t.revenue_id                                                            AS revenueid, " +
                "                            trf.revenue                                                             AS revenuevalue, " +
                "                            0                                                                       AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue, " +
                "                            0                                                                       AS revenuerigettato, " +
                "                            0                                                                       AS clicknumber, " +
                "                            0                                                                       AS clicknumberrigettato, " +
                "                            0                                                                       AS impressionnumber, " +
                "                            1                                                                       AS leadnumber, " +
                "                            0                                                                       AS leadnumberrigettato, " +
                "                            t.data                                                                  AS data, " +
                "                            CAST(t.wallet_id as bigint)                                             AS walletid, " +
                "                            t.payout_present                                                        AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                             AS payoutid, " +
                "                            t.payout_reference                                                      AS payoutreference " +
                "                     FROM t_transaction_cpl t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 72 " +
                "                         OR t.status_id = 73) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                    AS rn, " +
                "                            CAST('CPL' AS text)                                                     AS tipo, " +
                "                            CAST(t.id as bigint)                                                    AS id, " +
                "                            t.creation_date                                                         AS creationdate, " +
                "                            date(t.date_time)                                                       AS datetime, " +
                "                            CAST(t.status_id as bigint)                                             AS statusid, " +
                "                            tdd.name                                                                AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                "                            td.name                                                                 AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                "                            ta.name                                                                 AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                "                            tadv.name                                                               AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                            AS channelid, " +
                "                            c.name                                                                  AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                "                            tc.name                                                                 AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                              AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                         AS commissionid, " +
                "                            tco.name                                                                AS commissionname, " +
                "                            0                                                                       AS commissionvalue, " +
                "                            tco.value                                                               AS commissionvaluerigettato, " +
                "                            0                                                                       AS value, " +
                "                            round(CAST(t.value AS numeric), 2)                                      AS valuerigettato, " +
                "                            t.revenue_id                                                            AS revenueid, " +
                "                            0                                                                       AS revenuevalue, " +
                "                            trf.revenue                                                             AS revenuevaluerigettato, " +
                "                            0                                                                       AS revenue, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenuerigettato, " +
                "                            0                                                                       AS clicknumber, " +
                "                            0                                                                       AS clicknumberrigettato, " +
                "                            0                                                                       AS impressionnumber, " +
                "                            0                                                                       AS leadnumber, " +
                "                            1                                                                       AS leadnumberrigettato, " +
                "                            t.data                                                                  AS data, " +
                "                            CAST(t.wallet_id as bigint)                                             AS walletid, " +
                "                            t.payout_present                                                        AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                             AS payoutid, " +
                "                            t.payout_reference                                                      AS payoutreference " +
                "                     FROM t_transaction_cpl t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (t.status_id = 74 " +
                "                         OR t.status_id = 70) " +
                "                       AND (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                "                     UNION " +
                "                     SELECT row_number() OVER ()                                                          AS rn, " +
                "                            CAST('CPM' AS text)                                                           AS tipo, " +
                "                            CAST(t.id as bigint)                                                          AS id, " +
                "                            t.creation_date                                                               AS creationdate, " +
                "                            date(t.date_time)                                                             AS datetime, " +
                "                            CAST(t.status_id as bigint)                                                   AS statusid, " +
                "                            tdd.name                                                                      AS statusname, " +
                "                            CAST(t.dictionary_id as bigint)                                               AS dictionaryid, " +
                "                            td.name                                                                       AS dictionaryname, " +
                "                            CAST(t.affiliate_id as bigint)                                                AS affiliateid, " +
                "                            ta.name                                                                       AS affiliatename, " +
                "                            CAST(tadv.id as bigint)                                                       AS advertiserid, " +
                "                            tadv.name                                                                     AS advertisername, " +
                "                            CAST(t.channel_id as bigint)                                                  AS channelid, " +
                "                            c.name                                                                        AS channelname, " +
                "                            CAST(t.campaign_id as bigint)                                                 AS campaignid, " +
                "                            tc.name                                                                       AS campaignname, " +
                "                            CAST(t.media_id as bigint)                                                    AS mediaid, " +
                "                            CAST(t.commission_id as bigint)                                               AS commissionid, " +
                "                            tco.name                                                                      AS commissionname, " +
                "                            tco.value                                                                     AS commissionvalue, " +
                "                            0                                                                             AS commissionvaluerigettato, " +
                "                            round(CAST(t.value AS numeric), 2)                                            AS value, " +
                "                            0                                                                             AS valuerigettato, " +
                "                            t.revenue_id                                                                  AS revenueid, " +
                "                            trf.revenue                                                                   AS revenuevalue, " +
                "                            0                                                                             AS revenuevaluerigettato, " +
                "                            round(CAST(trf.revenue AS numeric) * CAST(t.impression_number AS numeric), 2) AS revenue, " +
                "                            0                                                                             AS revenuerigettato, " +
                "                            0                                                                             AS clicknumber, " +
                "                            0                                                                             AS clicknumberrigettato, " +
                "                            t.impression_number                                                           AS impressionnumber, " +
                "                            0                                                                             AS leadnumber, " +
                "                            0                                                                             AS leadnumberrigettato, " +
                "                            CAST(NULL AS character varying)                                               AS data, " +
                "                            CAST(t.wallet_id as bigint)                                                   AS walletid, " +
                "                            t.payout_present                                                              AS payoutpresent, " +
                "                            CAST(t.payout_id as bigint)                                                   AS payoutid, " +
                "                            t.payout_reference                                                            AS payoutreference " +
                "                     FROM t_transaction_cpm t " +
                "                              LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                "                              LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                "                              LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                "                              LEFT JOIN t_channel c ON t.channel_id = c.id " +
                "                              LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                "                              LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                "                              LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                "                              LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                "                     WHERE (cast(:dateFrom as timestamp) IS NULL OR (:dateFrom <= t.date_time)) " +
                "                       AND (cast(:dateTo as timestamp) IS NULL OR (:dateTo >= t.date_time)) " +
                "                       AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                "                       AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                "                       AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                "                       AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                "                       AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                "                       AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId)))) " +
                "SELECT transazioni.affiliateid                                                                                               as affiliateid, " +
                "       transazioni.affiliatename                                                                                             as affiliatename, " +
                "       transazioni.channelid                                                                                                 as channelid, " +
                "       transazioni.channelname                                                                                               as channelname, " +
                "       transazioni.campaignid                                                                                                as campaignId, " +
                "       transazioni.campaignname                                                                                              as campaignName, " +
                "       COALESCE(SUM(transazioni.impressionnumber), 0)                                                                        as impressionNumber, " +
                "       COALESCE(SUM(transazioni.clicknumber), 0)                                                                             as clickNumber, " +
                "       COALESCE(SUM(transazioni.clicknumberrigettato), 0)                                                                    as clickNumberRigettato, " +
                "       COALESCE(SUM(transazioni.leadnumber), 0)                                                                              as leadNumber, " +
                "       COALESCE(SUM(transazioni.leadnumberrigettato), 0)                                                                     as leadNumberRigettato, " +
                "       COALESCE(round(SUM(transazioni.clicknumber) / NULLIF(SUM(transazioni.impressionnumber), 0) * 100, 2), 0)              as CTR, " +
                "       COALESCE(round(CAST((SUM(transazioni.leadnumber) / NULLIF(SUM(transazioni.clicknumber), 0) * 100) AS numeric), 2), 0) as LR, " +
                "       COALESCE(round(CAST((SUM(transazioni.leadnumber) / NULLIF(SUM(transazioni.clicknumber), 0) * 100) AS numeric), 2), 0) as LR, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) AS numeric), 2), 0)                                                        as commission, " +
                "       COALESCE(round(CAST(SUM(transazioni.valuerigettato) AS numeric), 2), 0)                                               as commissionRigettato, " +
                "       COALESCE(round(CAST(SUM(transazioni.revenue) AS numeric), 2), 0)                                                      as revenue, " +
                "       COALESCE(round(CAST(SUM(transazioni.revenuerigettato) AS numeric), 2), 0)                                             as revenueRigettato, " +
                "       COALESCE(round(CAST((SUM(transazioni.revenue) - SUM(transazioni.value)) AS numeric), 2), 0)                           as margine, " +
                "       COALESCE(round(CAST((SUM(transazioni.revenue) - SUM(transazioni.value)) AS numeric) / CAST(SUM(NULLIF(transazioni.revenue, 0)) AS numeric) * " +
                "                      100, 2), 0)                                                                                            as marginePC, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.impressionnumber), 0) * 1000 AS numeric), 2), 0)  as ecpm, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.clicknumber), 0) AS numeric), 2), 0)              as ecpc, " +
                "       COALESCE(round(CAST(SUM(transazioni.value) / NULLIF(SUM(transazioni.leadnumber), 0) AS numeric), 2), 0)               as ecpl " +
                " " +
                "FROM transazioni " +
                "GROUP BY transazioni.affiliateid, transazioni.affiliatename, transazioni.channelname, transazioni.channelid, transazioni.campaignName, transazioni.campaignId " +
                "ORDER BY transazioni.affiliatename ASC;   ",
        resultSetMapping = "Mapping.searchReportAffiliateChannelCampaign")
@SqlResultSetMapping(name = "Mapping.searchReportAffiliateChannelCampaign",
        classes = @ConstructorResult(targetClass = ReportAffiliatesChannelCampaignDTO.class,
                columns = {
                        @ColumnResult(name = "affiliateId", type = Long.class),
                        @ColumnResult(name = "affiliateName", type = String.class),
                        @ColumnResult(name = "channelId", type = Long.class),
                        @ColumnResult(name = "channelName", type = String.class),
                        @ColumnResult(name = "campaignId", type = Long.class),
                        @ColumnResult(name = "campaignName", type = String.class),
                        @ColumnResult(name = "commission", type = Double.class),
                        @ColumnResult(name = "commissionRigettato", type = Double.class),
                        @ColumnResult(name = "revenue", type = Double.class),
                        @ColumnResult(name = "revenueRigettato", type = Double.class),
                        @ColumnResult(name = "margine", type = Double.class),
                        @ColumnResult(name = "marginePC", type = Double.class),
                        @ColumnResult(name = "ecpm", type = String.class),
                        @ColumnResult(name = "ecpc", type = String.class),
                        @ColumnResult(name = "ecpl", type = String.class),
                        @ColumnResult(name = "impressionNumber", type = Long.class),
                        @ColumnResult(name = "clickNumber", type = Long.class),
                        @ColumnResult(name = "leadNumber", type = Long.class),
                        @ColumnResult(name = "ctr", type = String.class),
                        @ColumnResult(name = "lr", type = String.class),
                        @ColumnResult(name = "leadNumberRigettato", type = Long.class),
                        @ColumnResult(name = "clickNumberRigettato", type = Long.class),
                }))


@Entity
@Table(name = "t_report")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@Getter
@Setter
@NoArgsConstructor
public class Report {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "report_type_id")
    private Long reportTypeId;
}