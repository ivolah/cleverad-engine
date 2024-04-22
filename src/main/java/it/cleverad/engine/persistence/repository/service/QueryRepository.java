package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.QueryTopElenco;
import it.cleverad.engine.persistence.model.service.QueryTransaction;
import it.cleverad.engine.persistence.model.service.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface QueryRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    @Query(nativeQuery = true, value =
            "WITH W as (SELECT DISTINCT row_number() OVER ()                                                                     AS rn,  " +
                    "                           CAST('CPC' AS text)                                                                      AS tipo,  " +
                    "                           t_transaction_cpc.id,  " +
                    "                           date(t_transaction_cpc.date_time)                                                        AS date_time,  " +
                    "                           tc.name                                                                                  AS campaign_name,  " +
                    "                           tc.id                                                                                    AS campaign_id,  " +
                    "                           tc.id_file                                                                               AS id_file,  " +
                    "                           t.value                                                                                  AS commission_value,  " +
                    "                           round(CAST(t_transaction_cpc.value AS numeric), 2)                                       AS value,  " +
                    "                           trf.revenue                                                                              AS revenue_value,  " +
                    "                           round(CAST(trf.revenue AS numeric) * CAST(t_transaction_cpc.click_number AS numeric), 2) AS revenue,  " +
                    "                           t_transaction_cpc.click_number                                                           AS clicknumber,  " +
                    "                           0                                                                                        AS impressionnumber,  " +
                    "                           0                                                                                        AS leadnumber  " +
                    "           FROM t_transaction_cpc  " +
                    "                    LEFT JOIN t_campaign tc ON t_transaction_cpc.campaign_id = tc.id  " +
                    "                    LEFT JOIN t_media tm ON t_transaction_cpc.media_id = tm.id  " +
                    "                    LEFT JOIN t_affiliate ta ON t_transaction_cpc.affiliate_id = ta.id  " +
                    "                    LEFT JOIN t_commision t ON t_transaction_cpc.commission_id = t.id  " +
                    "                    LEFT JOIN t_channel c ON t_transaction_cpc.channel_id = c.id  " +
                    "                    LEFT JOIN t_dictionary td ON t_transaction_cpc.dictionary_id = td.id  " +
                    "                    LEFT JOIN t_dictionary ts ON t_transaction_cpc.status_id = ts.id  " +
                    "                    LEFT JOIN t_revenuefactor trf ON t_transaction_cpc.revenue_id = trf.id  " +
                    "           WHERE (t_transaction_cpc.status_id = 72  " +
                    "               OR t_transaction_cpc.status_id = 73)  " +
                    "             AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t_transaction_cpc.date_time))  " +
                    "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t_transaction_cpc.date_time))  " +
                    "             AND ((:affiliateId) IS NULL OR (t_transaction_cpc.affiliate_id = (:affiliateId)))  " +
                    "             AND ((:campaignid) IS NULL OR (t_transaction_cpc.campaign_id = (:campaignid)))  " +
                    "             AND ((:advertiserid) IS NULL OR (tc.advertiser_id = (:advertiserid)))  " +
                    "  " +
                    "           UNION  " +
                    "           SELECT DISTINCT row_number() OVER ()              AS rn,  " +
                    "                           CAST('CPC' AS text)               AS tipo,  " +
                    "                           t_transaction_cpc.id,  " +
                    "                           date(t_transaction_cpc.date_time) AS date_time,  " +
                    "                           tc.name                           AS campaign_name,  " +
                    "                           tc.id                             AS campaign_id,  " +
                    "                           tc.id_file                        AS id_file,  " +
                    "                           0                                 AS commission_value,  " +
                    "                           0                                 AS value,  " +
                    "                           0                                 AS revenue_value,  " +
                    "                           0                                 AS revenue,  " +
                    "                           0                                 AS clicknumber,  " +
                    "                           0                                 AS impressionnumber,  " +
                    "                           0                                 AS leadnumber  " +
                    "           FROM t_transaction_cpc  " +
                    "                    LEFT JOIN t_campaign tc ON t_transaction_cpc.campaign_id = tc.id  " +
                    "                    LEFT JOIN t_media tm ON t_transaction_cpc.media_id = tm.id  " +
                    "                    LEFT JOIN t_affiliate ta ON t_transaction_cpc.affiliate_id = ta.id  " +
                    "                    LEFT JOIN t_commision t ON t_transaction_cpc.commission_id = t.id  " +
                    "                    LEFT JOIN t_channel c ON t_transaction_cpc.channel_id = c.id  " +
                    "                    LEFT JOIN t_dictionary td ON t_transaction_cpc.dictionary_id = td.id  " +
                    "                    LEFT JOIN t_dictionary tdd ON t_transaction_cpc.status_id = tdd.id  " +
                    "                    LEFT JOIN t_revenuefactor trf ON t_transaction_cpc.revenue_id = trf.id  " +
                    "           WHERE t_transaction_cpc.status_id = 74  " +
                    "              OR t_transaction_cpc.status_id = 70  " +
                    "               AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t_transaction_cpc.date_time))  " +
                    "               AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t_transaction_cpc.date_time))  " +
                    "               AND ((:affiliateId) IS NULL OR (t_transaction_cpc.affiliate_id = (:affiliateId)))  " +
                    "               AND ((:campaignid) IS NULL OR (t_transaction_cpc.campaign_id = (:campaignid)))  " +
                    "               AND ((:advertiserid) IS NULL OR (tc.advertiser_id = (:advertiserid)))  " +
                    "           UNION  " +
                    "           SELECT DISTINCT row_number() OVER ()                                                                    AS rn,  " +
                    "                           CAST('CPL' AS text)                                                                     AS tipo,  " +
                    "                           t_transaction_cpl.id,  " +
                    "                           date(t_transaction_cpl.date_time)                                                       AS date_time,  " +
                    "                           tc.name                                                                                 AS campaign_name,  " +
                    "                           tc.id                                                                                   AS campaign_id,  " +
                    "                           tc.id_file                                                                              AS id_file,  " +
                    "                           t.value                                                                                 AS commission_value,  " +
                    "                           round(CAST(t_transaction_cpl.value AS numeric), 2)                                      AS value,  " +
                    "                           trf.revenue                                                                             AS revenue_value,  " +
                    "                           round(CAST(trf.revenue AS numeric) * CAST(t_transaction_cpl.lead_number AS numeric), 2) AS revenue,  " +
                    "                           0                                                                                       AS clicknumber,  " +
                    "                           0                                                                                       AS impressionnumber,  " +
                    "                           t_transaction_cpl.lead_number                                                           AS leadnumber  " +
                    "           FROM t_transaction_cpl  " +
                    "                    LEFT JOIN t_campaign tc ON t_transaction_cpl.campaign_id = tc.id  " +
                    "                    LEFT JOIN t_media tm ON t_transaction_cpl.media_id = tm.id  " +
                    "                    LEFT JOIN t_affiliate ta ON t_transaction_cpl.affiliate_id = ta.id  " +
                    "                    LEFT JOIN t_commision t ON t_transaction_cpl.commission_id = t.id  " +
                    "                    LEFT JOIN t_channel c ON t_transaction_cpl.channel_id = c.id  " +
                    "                    LEFT JOIN t_dictionary td ON t_transaction_cpl.dictionary_id = td.id  " +
                    "                    LEFT JOIN t_dictionary tdd ON t_transaction_cpl.status_id = tdd.id  " +
                    "                    LEFT JOIN t_revenuefactor trf ON t_transaction_cpl.revenue_id = trf.id  " +
                    "           WHERE t_transaction_cpl.status_id = 72  " +
                    "              OR t_transaction_cpl.status_id = 73  " +
                    "               AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t_transaction_cpl.date_time))  " +
                    "               AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t_transaction_cpl.date_time))  " +
                    "               AND ((:affiliateId) IS NULL OR (t_transaction_cpl.affiliate_id = (:affiliateId)))  " +
                    "               AND ((:campaignid) IS NULL OR (t_transaction_cpl.campaign_id = (:campaignid)))  " +
                    "               AND ((:advertiserid) IS NULL OR (tc.advertiser_id = (:advertiserid)))  " +
                    "           UNION  " +
                    "           SELECT DISTINCT row_number() OVER ()              AS rn,  " +
                    "                           CAST('CPL' AS text)               AS tipo,  " +
                    "                           t_transaction_cpl.id,  " +
                    "                           date(t_transaction_cpl.date_time) AS date_time,  " +
                    "                           tc.name                           AS campaign_name,  " +
                    "                           tc.id                             AS campaign_id,  " +
                    "                           tc.id_file                        AS id_file,  " +
                    "                           0                                 AS commission_value,  " +
                    "                           0                                 AS value,  " +
                    "                           0                                 AS revenue_value,  " +
                    "                           0                                 AS revenue,  " +
                    "                           0                                 AS clicknumber,  " +
                    "                           0                                 AS impressionnumber,  " +
                    "                           0                                 AS leadnumber  " +
                    "           FROM t_transaction_cpl  " +
                    "                    LEFT JOIN t_campaign tc ON t_transaction_cpl.campaign_id = tc.id  " +
                    "                    LEFT JOIN t_media tm ON t_transaction_cpl.media_id = tm.id  " +
                    "                    LEFT JOIN t_affiliate ta ON t_transaction_cpl.affiliate_id = ta.id  " +
                    "                    LEFT JOIN t_commision t ON t_transaction_cpl.commission_id = t.id  " +
                    "                    LEFT JOIN t_channel c ON t_transaction_cpl.channel_id = c.id  " +
                    "                    LEFT JOIN t_dictionary td ON t_transaction_cpl.dictionary_id = td.id  " +
                    "                    LEFT JOIN t_dictionary tdd ON t_transaction_cpl.status_id = tdd.id  " +
                    "                    LEFT JOIN t_revenuefactor trf ON t_transaction_cpl.revenue_id = trf.id  " +
                    "           WHERE t_transaction_cpl.status_id = 74  " +
                    "              OR t_transaction_cpl.status_id = 70  " +
                    "               AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t_transaction_cpl.date_time))  " +
                    "               AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t_transaction_cpl.date_time))  " +
                    "               AND ((:affiliateId) IS NULL OR (t_transaction_cpl.affiliate_id = (:affiliateId)))  " +
                    "               AND ((:campaignid) IS NULL OR (t_transaction_cpl.campaign_id = (:campaignid)))  " +
                    "               AND ((:advertiserid) IS NULL OR (tc.advertiser_id = (:advertiserid)))  " +
                    "           UNION  " +
                    "           SELECT DISTINCT row_number() OVER ()                                                                          AS rn,  " +
                    "                           CAST('CPM' AS text)                                                                           AS tipo,  " +
                    "                           t_transaction_cpm.id,  " +
                    "                           date(t_transaction_cpm.date_time)                                                             AS date_time,  " +
                    "                           tc.name                                                                                       AS campaign_name,  " +
                    "                           tc.id                                                                                         AS campaign_id,  " +
                    "                           tc.id_file                                                                                    AS id_file,  " +
                    "                           t.value                                                                                       AS commission_value,  " +
                    "                           round(CAST(t_transaction_cpm.value AS numeric), 2)                                            AS value,  " +
                    "                           round(CAST(trf.revenue AS numeric) * CAST(t_transaction_cpm.impression_number AS numeric), 2) AS revenue,  " +
                    "                           0                                                                                             AS revenue_rigettato,  " +
                    "                           0                                                                                             AS clicknumber,  " +
                    "                           t_transaction_cpm.impression_number                                                           as impressionnumber,  " +
                    "                           0                                                                                             AS leadnumber  " +
                    "           FROM t_transaction_cpm  " +
                    "                    LEFT JOIN t_campaign tc ON t_transaction_cpm.campaign_id = tc.id  " +
                    "                    LEFT JOIN t_media tm ON t_transaction_cpm.media_id = tm.id  " +
                    "                    LEFT JOIN t_affiliate ta ON t_transaction_cpm.affiliate_id = ta.id  " +
                    "                    LEFT JOIN t_commision t ON t_transaction_cpm.commission_id = t.id  " +
                    "                    LEFT JOIN t_channel c ON t_transaction_cpm.channel_id = c.id  " +
                    "                    LEFT JOIN t_dictionary td ON t_transaction_cpm.dictionary_id = td.id  " +
                    "                    LEFT JOIN t_revenuefactor trf ON t_transaction_cpm.revenue_id = trf.id  " +
                    "                    LEFT JOIN t_dictionary tdd ON t_transaction_cpm.status_id = tdd.id  " +
                    "           WHERE ((:advertiserid) IS NULL OR (tc.advertiser_id = (:advertiserid)))  " +
                    "             AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t_transaction_cpm.date_time))  " +
                    "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t_transaction_cpm.date_time))  " +
                    "             AND ((:affiliateId) IS NULL OR (t_transaction_cpm.affiliate_id = (:affiliateId)))  " +
                    "             AND ((:campaignid) IS NULL OR (t_transaction_cpm.campaign_id = (:campaignid))))  " +
                    "  " +
                    "SELECT campaign_id                        as campaignid,  " +
                    "       campaign_name                      as campaignname,  " +
                    "       id_file                            as fileid,  " +
                    "       COALESCE(SUM(impressionnumber), 0) as impressionnumber,  " +
                    "       COALESCE(SUM(clicknumber), 0)      as clicknumber,  " +
                    "       COALESCE(SUM(leadnumber), 0)       as leadnumber  " +
                    "from w  " +
                    "group by campaignid, campaignname, fileid  " +
                    "order by impressionnumber desc  " +
                    "limit 10;"
    )
    List<QueryTopElenco> listaTopCampagneSortate(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid, @Param("advertiserid") Long advertiserid);

    //===========================================================================================================================================================================

    @Query(nativeQuery = true, value = "SELECT DISTINCT row_number() OVER ()                                                     AS rn, " +
            "                CAST('CPC' as text)                                                      AS tipo, " +
            "                CAST(t.id as bigint)                                                     AS id, " +
            "                t.creation_date                                                          AS creationdate, " +
            "                date(t.date_time)                                                        AS datetime, " +
            "                CAST(t.status_id as bigint)                                              AS statusid, " +
            "                tdd.name                                                                 AS statusname, " +
            "                CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
            "                td.name                                                                  AS dictionaryname, " +
            "                CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
            "                ta.name                                                                  AS affiliatename, " +
            "                CAST(tadv.id as bigint)                                                  AS advertiserid, " +
            "                tadv.name                                                                AS advertisername, " +
            "                CAST(t.channel_id as bigint)                                             AS channelid, " +
            "                c.name                                                                   AS channelname, " +
            "                CAST(t.campaign_id as bigint)                                            AS campaignid, " +
            "                tc.name                                                                  AS campaignname, " +
            "                CAST(t.media_id as bigint)                                               AS mediaid, " +
            "                tm.name                                                                  AS medianame, " +
            "                CAST(t.commission_id as bigint)                                          AS commissionid, " +
            "                tco.name                                                                 AS commissionname, " +
            "                tco.value                                                                AS commissionvalue, " +
            "                CAST(0 as numeric)                                                       AS commissionvaluerigettato, " +
            "                round(CAST(t.value AS numeric), 2)                                       AS value, " +
            "                CAST(0 as numeric)                                                       AS valuerigettato, " +
            "                CAST(t.revenue_id as bigint)                                             AS revenueid, " +
            "                trf.revenue                                                              AS revenuevalue, " +
            "                CAST(0 as numeric)                                                       AS revenuevaluerigettato, " +
            "                round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue, " +
            "                CAST(0 as numeric)                                                       AS revenuerigettato, " +
            "                t.click_number                                                           AS clicknumber, " +
            "                CAST(0 as bigint)                                                        AS clicknumberrigettato, " +
            "                CAST(0 as bigint)                                                        AS impressionnumber, " +
            "                CAST(0 as bigint)                                                        AS leadnumber, " +
            "                CAST(0 as bigint)                                                        AS leadnumberrigettato, " +
            "                CAST(NULL AS character varying)                                          AS data, " +
            "                CAST(t.wallet_id as bigint)                                              AS walletid, " +
            "                t.payout_present                                                         AS payoutpresent, " +
            "                CAST(t.payout_id as bigint)                                              AS payoutid, " +
            "                t.payout_reference                                                       AS payoutreference " +
            "FROM t_transaction_cpc as t " +
            "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
            "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
            "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
            "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
            "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
            "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
            "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
            "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
            "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
            "WHERE (t.status_id = 72 " +
            "    OR t.status_id = 73) " +
            "  AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
            "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
            "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
            "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
            "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
            "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
            "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
            "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
            "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
            "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
            "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
            "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
            "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
            "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
            "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
            "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
            "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))) " +
            "UNION " +
            "SELECT DISTINCT row_number() OVER ()                                                     AS rn, " +
            "                CAST('CPC' AS text)                                                      AS tipo, " +
            "                CAST(t.id as bigint)                                                     AS id, " +
            "                t.creation_date                                                          AS creationdate, " +
            "                date(t.date_time)                                                        AS datetime, " +
            "                CAST(t.status_id as bigint)                                              AS statusid, " +
            "                tdd.name                                                                 AS statusname, " +
            "                CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
            "                td.name                                                                  AS dictionaryname, " +
            "                CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
            "                ta.name                                                                  AS affiliatename, " +
            "                CAST(tadv.id as bigint)                                                  AS advertiserid, " +
            "                tadv.name                                                                AS advertisername, " +
            "                CAST(t.channel_id as bigint)                                             AS channelid, " +
            "                c.name                                                                   AS channelname, " +
            "                CAST(t.campaign_id as bigint)                                            AS campaignid, " +
            "                tc.name                                                                  AS campaignname, " +
            "                CAST(t.media_id as bigint)                                               AS mediaid, " +
            "                tm.name                                                                  AS medianame, " +
            "                CAST(t.commission_id as bigint)                                          AS commissionid, " +
            "                tco.name                                                                 AS commissionname, " +
            "                0                                                                        AS commission_value, " +
            "                tco.value                                                                AS commission_value_rigettato, " +
            "                0                                                                        AS value, " +
            "                round(CAST(t.value AS numeric), 2)                                       AS value_rigettato, " +
            "                t.revenue_id                                                             AS revenueid, " +
            "                0                                                                        AS revenue_value, " +
            "                trf.revenue                                                              AS revenue_value_rigettato, " +
            "                0                                                                        AS revenue, " +
            "                round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue_rigettato, " +
            "                0                                                                        AS click_number, " +
            "                t.click_number                                                           AS click_number_rigettato, " +
            "                0                                                                        AS impression_number, " +
            "                0                                                                        AS lead_number, " +
            "                0                                                                        AS lead_number_rigettato, " +
            "                CAST(NULL AS character varying)                                          AS data, " +
            "                CAST(t.wallet_id as bigint)                                              AS walletid, " +
            "                t.payout_present                                                         AS payoutpresent, " +
            "                CAST(t.payout_id as bigint)                                              AS payoutid, " +
            "                t.payout_reference                                                       AS payoutreference " +
            "FROM t_transaction_cpc t " +
            "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
            "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
            "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
            "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
            "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
            "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
            "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
            "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
            "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
            "WHERE (t.status_id = 74 " +
            "    OR t.status_id = 70) " +
            "  AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
            "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
            "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
            "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
            "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
            "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
            "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
            "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
            "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
            "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
            "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
            "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
            "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
            "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
            "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
            "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
            "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))) " +
            "UNION " +
            "SELECT DISTINCT row_number() OVER ()                                                    AS rn, " +
            "                CAST('CPL' AS text)                                                     AS tipo, " +
            "                CAST(t.id as bigint)                                                    AS id, " +
            "                t.creation_date                                                         AS creationdate, " +
            "                date(t.date_time)                                                       AS datetime, " +
            "                CAST(t.status_id as bigint)                                             AS statusid, " +
            "                tdd.name                                                                AS statusname, " +
            "                CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
            "                td.name                                                                 AS dictionaryname, " +
            "                CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
            "                ta.name                                                                 AS affiliatename, " +
            "                CAST(tadv.id as bigint)                                                 AS advertiserid, " +
            "                tadv.name                                                               AS advertisername, " +
            "                CAST(t.channel_id as bigint)                                            AS channelid, " +
            "                c.name                                                                  AS channelname, " +
            "                CAST(t.campaign_id as bigint)                                           AS campaignid, " +
            "                tc.name                                                                 AS campaignname, " +
            "                CAST(t.media_id as bigint)                                              AS mediaid, " +
            "                tm.name                                                                 AS medianame, " +
            "                CAST(t.commission_id as bigint)                                         AS commissionid, " +
            "                tco.name                                                                AS commissionname, " +
            "                tco.value                                                               AS commission_value, " +
            "                0                                                                       AS commission_value_rigettato, " +
            "                round(CAST(t.value AS numeric), 2)                                      AS value, " +
            "                0                                                                       AS value_rigettato, " +
            "                t.revenue_id                                                            AS revenueid, " +
            "                trf.revenue                                                             AS revenue_value, " +
            "                0                                                                       AS revenue_value_rigettato, " +
            "                round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue, " +
            "                0                                                                       AS revenue_rigettato, " +
            "                0                                                                       AS click_number, " +
            "                0                                                                       AS click_number_rigettato, " +
            "                0                                                                       AS impression_number, " +
            "                t.lead_number                                                           AS leadnumber, " +
            "                0                                                                       AS lead_number_rigettato, " +
            "                t.data                                                                  AS data, " +
            "                CAST(t.wallet_id as bigint)                                             AS walletid, " +
            "                t.payout_present                                                        AS payoutpresent, " +
            "                CAST(t.payout_id as bigint)                                             AS payoutid, " +
            "                t.payout_reference                                                      AS payoutreference " +
            "FROM t_transaction_cpl t " +
            "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
            "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
            "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
            "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
            "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
            "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
            "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
            "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
            "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
            "WHERE (t.status_id = 72 " +
            "    OR t.status_id = 73) " +
            "  AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
            "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
            "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
            "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
            "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
            "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
            "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
            "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
            "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
            "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
            "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
            "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
            "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
            "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
            "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
            "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
            "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))) " +
            " " +
            "UNION " +
            "SELECT DISTINCT row_number() OVER ()                                                    AS rn, " +
            "                CAST('CPL' AS text)                                                     AS tipo, " +
            "                CAST(t.id as bigint)                                                    AS id, " +
            "                t.creation_date                                                         AS creationdate, " +
            "                date(t.date_time)                                                       AS datetime, " +
            "                CAST(t.status_id as bigint)                                             AS statusid, " +
            "                tdd.name                                                                AS statusname, " +
            "                CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
            "                td.name                                                                 AS dictionaryname, " +
            "                CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
            "                ta.name                                                                 AS affiliatename, " +
            "                CAST(tadv.id as bigint)                                                 AS advertiserid, " +
            "                tadv.name                                                               AS advertisername, " +
            "                CAST(t.channel_id as bigint)                                            AS channelid, " +
            "                c.name                                                                  AS channelname, " +
            "                CAST(t.campaign_id as bigint)                                           AS campaignid, " +
            "                tc.name                                                                 AS campaignname, " +
            "                CAST(t.media_id as bigint)                                              AS mediaid, " +
            "                tm.name                                                                 AS medianame, " +
            "                CAST(t.commission_id as bigint)                                         AS commissionid, " +
            "                tco.name                                                                AS commissionname, " +
            "                0                                                                       AS commission_value, " +
            "                tco.value                                                               AS commission_value_rigettato, " +
            "                0                                                                       AS value, " +
            "                round(CAST(t.value AS numeric), 2)                                      AS value_rigettato, " +
            "                t.revenue_id                                                            AS revenueid, " +
            "                0                                                                       AS revenue_value, " +
            "                trf.revenue                                                             AS revenue_value_rigettato, " +
            "                0                                                                       AS revenue, " +
            "                round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue_rigettato, " +
            "                0                                                                       AS click_number, " +
            "                0                                                                       AS click_number_rigettato, " +
            "                0                                                                       AS impression_number, " +
            "                0                                                                       AS lead_number, " +
            "                t.lead_number                                                           AS lead_number_rigettato, " +
            "                t.data                                                                  AS data, " +
            "                CAST(t.wallet_id as bigint)                                             AS walletid, " +
            "                t.payout_present                                                        AS payoutpresent, " +
            "                CAST(t.payout_id as bigint)                                             AS payoutid, " +
            "                t.payout_reference                                                      AS payoutreference " +
            "FROM t_transaction_cpl t " +
            "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
            "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
            "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
            "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
            "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
            "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
            "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
            "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
            "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
            "WHERE (t.status_id = 74 " +
            "    OR t.status_id = 70) " +
            "  AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
            "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
            "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
            "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
            "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
            "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
            "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
            "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
            "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
            "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
            "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
            "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
            "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
            "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
            "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
            "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
            "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))) " +
            " " +
            "UNION " +
            "SELECT DISTINCT row_number() OVER ()                                                          AS rn, " +
            "                CAST('CPM' AS text)                                                           AS tipo, " +
            "                CAST(t.id as bigint)                                                          AS id, " +
            "                t.creation_date                                                               AS creationdate, " +
            "                date(t.date_time)                                                             AS datetime, " +
            "                CAST(t.status_id as bigint)                                                   AS statusid, " +
            "                tdd.name                                                                      AS statusname, " +
            "                CAST(t.dictionary_id as bigint)                                               AS dictionaryid, " +
            "                td.name                                                                       AS dictionaryname, " +
            "                CAST(t.affiliate_id as bigint)                                                AS affiliateid, " +
            "                ta.name                                                                       AS affiliatename, " +
            "                CAST(tadv.id as bigint)                                                       AS advertiserid, " +
            "                tadv.name                                                                     AS advertisername, " +
            "                CAST(t.channel_id as bigint)                                                  AS channelid, " +
            "                c.name                                                                        AS channelname, " +
            "                CAST(t.campaign_id as bigint)                                                 AS campaignid, " +
            "                tc.name                                                                       AS campaignname, " +
            "                CAST(t.media_id as bigint)                                                    AS mediaid, " +
            "                tm.name                                                                       AS medianame, " +
            "                CAST(t.commission_id as bigint)                                               AS commissionid, " +
            "                tco.name                                                                      AS commissionname, " +
            "                tco.value                                                                     AS commission_value, " +
            "                0                                                                             AS commission_value_rigettato, " +
            "                round(CAST(t.value AS numeric), 2)                                            AS value, " +
            "                0                                                                             AS value_rigettato, " +
            "                t.revenue_id                                                                  AS revenueid, " +
            "                trf.revenue                                                                   AS revenue_value, " +
            "                0                                                                             AS revenue_value_rigettato, " +
            "                round(CAST(trf.revenue AS numeric) * CAST(t.impression_number AS numeric), 2) AS revenue, " +
            "                0                                                                             AS revenue_rigettato, " +
            "                0                                                                             AS click_number, " +
            "                0                                                                             AS click_number_rigettato, " +
            "                t.impression_number                                                           AS impressionnumber, " +
            "                0                                                                             AS lead_number, " +
            "                0                                                                             AS lead_number_rigettato, " +
            "                CAST(NULL AS character varying)                                               AS data, " +
            "                CAST(t.wallet_id as bigint)                                                   AS walletid, " +
            "                t.payout_present                                                              AS payoutpresent, " +
            "                CAST(t.payout_id as bigint)                                                   AS payoutid, " +
            "                t.payout_reference                                                            AS payoutreference " +
            "FROM t_transaction_cpm t " +
            "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
            "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
            "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
            "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
            "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
            "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
            "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
            "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
            "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
            "WHERE (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
            "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
            "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
            "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
            "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
            "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
            "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
            "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
            "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
            "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
            "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
            "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
            "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
            "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
            "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
            "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
            "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))); ;"
    )
    List<QueryTransaction> listaTransazioni(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("statusId") Long statusId,
            @Param("dictionaryId") Long dictionaryId,
            @Param("affiliateId") Long affiliateId,
            @Param("channelId") Long channelId,
            @Param("campaignId") Long campaignId,
            @Param("mediaId") Long mediaId,
            @Param("commissionId") Long commissionId,
            @Param("revenueId") Long revenueId,
            @Param("payoutPresent") Boolean payoutPresent,
            @Param("payoutId") Long payoutId,
            @Param("advertiserId") Long advertiserId,
            @Param("valueNotZero") Boolean valueNotZero,
            @Param("inDictionaryId") List<Long> inDictionaryId,
            @Param("notInDictionaryId") List<Long> notInDictionaryId,
            @Param("inStausId") List<Long> inStausId,
            @Param("notInStausId") List<Long> notInStausId
    );

    //=========================================================================================================================
    // CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC
    //=========================================================================================================================

    @Query(nativeQuery = true, value = "SELECT DISTINCT row_number() OVER ()                                                     AS rn, " +
            "                CAST('CPC' as text)                                                      AS tipo, " +
            "                CAST(t.id as bigint)                                                     AS id, " +
            "                t.creation_date                                                          AS creationdate, " +
            "                date(t.date_time)                                                        AS datetime, " +
            "                CAST(t.status_id as bigint)                                              AS statusid, " +
            "                tdd.name                                                                 AS statusname, " +
            "                CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
            "                td.name                                                                  AS dictionaryname, " +
            "                CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
            "                ta.name                                                                  AS affiliatename, " +
            "                CAST(tadv.id as bigint)                                                  AS advertiserid, " +
            "                tadv.name                                                                AS advertisername, " +
            "                CAST(t.channel_id as bigint)                                             AS channelid, " +
            "                c.name                                                                   AS channelname, " +
            "                CAST(t.campaign_id as bigint)                                            AS campaignid, " +
            "                tc.name                                                                  AS campaignname, " +
            "                CAST(t.media_id as bigint)                                               AS mediaid, " +
            "                tm.name                                                                  AS medianame, " +
            "                CAST(t.commission_id as bigint)                                          AS commissionid, " +
            "                tco.name                                                                 AS commissionname, " +
            "                tco.value                                                                AS commissionvalue, " +
            "                CAST(0 as numeric)                                                       AS commissionvaluerigettato, " +
            "                round(CAST(t.value AS numeric), 2)                                       AS value, " +
            "                CAST(0 as numeric)                                                       AS valuerigettato, " +
            "                CAST(t.revenue_id as bigint)                                             AS revenueid, " +
            "                trf.revenue                                                              AS revenuevalue, " +
            "                CAST(0 as numeric)                                                       AS revenuevaluerigettato, " +
            "                round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue, " +
            "                CAST(0 as numeric)                                                       AS revenuerigettato, " +
            "                t.click_number                                                           AS clicknumber, " +
            "                CAST(0 as bigint)                                                        AS clicknumberrigettato, " +
            "                CAST(0 as bigint)                                                        AS impressionnumber, " +
            "                CAST(0 as bigint)                                                        AS leadnumber, " +
            "                CAST(0 as bigint)                                                        AS leadnumberrigettato, " +
            "                CAST(NULL AS character varying)                                          AS data, " +
            "                CAST(t.wallet_id as bigint)                                              AS walletid, " +
            "                t.payout_present                                                         AS payoutpresent, " +
            "                CAST(t.payout_id as bigint)                                              AS payoutid, " +
            "                t.payout_reference                                                       AS payoutreference " +
            "FROM t_transaction_cpc as t " +
            "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
            "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
            "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
            "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
            "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
            "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
            "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
            "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
            "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
            "WHERE (t.status_id = 72 " +
            "    OR t.status_id = 73) " +
            "  AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
            "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
            "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
            "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
            "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
            "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
            "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
            "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
            "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
            "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
            "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
            "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
            "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
            "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
            "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
            "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
            "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))) " +
            "UNION " +
            "SELECT DISTINCT row_number() OVER ()                                                     AS rn, " +
            "                CAST('CPC' AS text)                                                      AS tipo, " +
            "                CAST(t.id as bigint)                                                     AS id, " +
            "                t.creation_date                                                          AS creationdate, " +
            "                date(t.date_time)                                                        AS datetime, " +
            "                CAST(t.status_id as bigint)                                              AS statusid, " +
            "                tdd.name                                                                 AS statusname, " +
            "                CAST(t.dictionary_id as bigint)                                          AS dictionaryid, " +
            "                td.name                                                                  AS dictionaryname, " +
            "                CAST(t.affiliate_id as bigint)                                           AS affiliateid, " +
            "                ta.name                                                                  AS affiliatename, " +
            "                CAST(tadv.id as bigint)                                                  AS advertiserid, " +
            "                tadv.name                                                                AS advertisername, " +
            "                CAST(t.channel_id as bigint)                                             AS channelid, " +
            "                c.name                                                                   AS channelname, " +
            "                CAST(t.campaign_id as bigint)                                            AS campaignid, " +
            "                tc.name                                                                  AS campaignname, " +
            "                CAST(t.media_id as bigint)                                               AS mediaid, " +
            "                tm.name                                                                  AS medianame, " +
            "                CAST(t.commission_id as bigint)                                          AS commissionid, " +
            "                tco.name                                                                 AS commissionname, " +
            "                0                                                                        AS commission_value, " +
            "                tco.value                                                                AS commission_value_rigettato, " +
            "                0                                                                        AS value, " +
            "                round(CAST(t.value AS numeric), 2)                                       AS value_rigettato, " +
            "                t.revenue_id                                                             AS revenueid, " +
            "                0                                                                        AS revenue_value, " +
            "                trf.revenue                                                              AS revenue_value_rigettato, " +
            "                0                                                                        AS revenue, " +
            "                round(CAST(trf.revenue AS numeric) * CAST(t.click_number AS numeric), 2) AS revenue_rigettato, " +
            "                0                                                                        AS click_number, " +
            "                t.click_number                                                           AS click_number_rigettato, " +
            "                0                                                                        AS impression_number, " +
            "                0                                                                        AS lead_number, " +
            "                0                                                                        AS lead_number_rigettato, " +
            "                CAST(NULL AS character varying)                                          AS data, " +
            "                CAST(t.wallet_id as bigint)                                              AS walletid, " +
            "                t.payout_present                                                         AS payoutpresent, " +
            "                CAST(t.payout_id as bigint)                                              AS payoutid, " +
            "                t.payout_reference                                                       AS payoutreference " +
            "FROM t_transaction_cpc t " +
            "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
            "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
            "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
            "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
            "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
            "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
            "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
            "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
            "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
            "WHERE (t.status_id = 74 " +
            "    OR t.status_id = 70) " +
            "  AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
            "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
            "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
            "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
            "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
            "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
            "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
            "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
            "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
            "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
            "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
            "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
            "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
            "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
            "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
            "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
            "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId)));"
    )
    List<QueryTransaction>  listaTransazioniCPC(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("statusId") Long statusId,
            @Param("dictionaryId") Long dictionaryId,
            @Param("affiliateId") Long affiliateId,
            @Param("channelId") Long channelId,
            @Param("campaignId") Long campaignId,
            @Param("mediaId") Long mediaId,
            @Param("commissionId") Long commissionId,
            @Param("revenueId") Long revenueId,
            @Param("payoutPresent") Boolean payoutPresent,
            @Param("payoutId") Long payoutId,
            @Param("advertiserId") Long advertiserId,
            @Param("valueNotZero") Boolean valueNotZero,
            @Param("inDictionaryId") List<Long> inDictionaryId,
            @Param("notInDictionaryId") List<Long> notInDictionaryId,
            @Param("inStausId") List<Long> inStausId,
            @Param("notInStausId") List<Long> notInStausId
    );

    //=========================================================================================================================
    // CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL
    //=========================================================================================================================

    @Query(nativeQuery = true, value =
            "SELECT DISTINCT row_number() OVER ()                                                    AS rn, " +
                    "                CAST('CPL' AS text)                                                     AS tipo, " +
                    "                CAST(t.id as bigint)                                                    AS id, " +
                    "                t.creation_date                                                         AS creationdate, " +
                    "                date(t.date_time)                                                       AS datetime, " +
                    "                CAST(t.status_id as bigint)                                             AS statusid, " +
                    "                tdd.name                                                                AS statusname, " +
                    "                CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                    "                td.name                                                                 AS dictionaryname, " +
                    "                CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                    "                ta.name                                                                 AS affiliatename, " +
                    "                CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                    "                tadv.name                                                               AS advertisername, " +
                    "                CAST(t.channel_id as bigint)                                            AS channelid, " +
                    "                c.name                                                                  AS channelname, " +
                    "                CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                    "                tc.name                                                                 AS campaignname, " +
                    "                CAST(t.media_id as bigint)                                              AS mediaid, " +
                    "                tm.name                                                                 AS medianame, " +
                    "                CAST(t.commission_id as bigint)                                         AS commissionid, " +
                    "                tco.name                                                                AS commissionname, " +
                    "                tco.value                                                               AS commission_value, " +
                    "                0                                                                       AS commission_value_rigettato, " +
                    "                round(CAST(t.value AS numeric), 2)                                      AS value, " +
                    "                0                                                                       AS value_rigettato, " +
                    "                t.revenue_id                                                            AS revenueid, " +
                    "                trf.revenue                                                             AS revenue_value, " +
                    "                0                                                                       AS revenue_value_rigettato, " +
                    "                round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue, " +
                    "                0                                                                       AS revenue_rigettato, " +
                    "                0                                                                       AS click_number, " +
                    "                0                                                                       AS click_number_rigettato, " +
                    "                0                                                                       AS impression_number, " +
                    "                t.lead_number                                                           AS leadnumber, " +
                    "                0                                                                       AS lead_number_rigettato, " +
                    "                t.data                                                                  AS data, " +
                    "                CAST(t.wallet_id as bigint)                                             AS walletid, " +
                    "                t.payout_present                                                        AS payoutpresent, " +
                    "                CAST(t.payout_id as bigint)                                             AS payoutid, " +
                    "                t.payout_reference                                                      AS payoutreference " +
                    "FROM t_transaction_cpl t " +
                    "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                    "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
                    "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                    "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                    "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
                    "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                    "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                    "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                    "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                    "WHERE (t.status_id = 72 " +
                    "    OR t.status_id = 73) " +
                    "  AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
                    "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
                    "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                    "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                    "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                    "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                    "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                    "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
                    "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
                    "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
                    "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
                    "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
                    "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                    "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
                    "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
                    "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
                    "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                    "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))) " +
                    " " +
                    "UNION " +
                    "SELECT DISTINCT row_number() OVER ()                                                    AS rn, " +
                    "                CAST('CPL' AS text)                                                     AS tipo, " +
                    "                CAST(t.id as bigint)                                                    AS id, " +
                    "                t.creation_date                                                         AS creationdate, " +
                    "                date(t.date_time)                                                       AS datetime, " +
                    "                CAST(t.status_id as bigint)                                             AS statusid, " +
                    "                tdd.name                                                                AS statusname, " +
                    "                CAST(t.dictionary_id as bigint)                                         AS dictionaryid, " +
                    "                td.name                                                                 AS dictionaryname, " +
                    "                CAST(t.affiliate_id as bigint)                                          AS affiliateid, " +
                    "                ta.name                                                                 AS affiliatename, " +
                    "                CAST(tadv.id as bigint)                                                 AS advertiserid, " +
                    "                tadv.name                                                               AS advertisername, " +
                    "                CAST(t.channel_id as bigint)                                            AS channelid, " +
                    "                c.name                                                                  AS channelname, " +
                    "                CAST(t.campaign_id as bigint)                                           AS campaignid, " +
                    "                tc.name                                                                 AS campaignname, " +
                    "                CAST(t.media_id as bigint)                                              AS mediaid, " +
                    "                tm.name                                                                 AS medianame, " +
                    "                CAST(t.commission_id as bigint)                                         AS commissionid, " +
                    "                tco.name                                                                AS commissionname, " +
                    "                0                                                                       AS commission_value, " +
                    "                tco.value                                                               AS commission_value_rigettato, " +
                    "                0                                                                       AS value, " +
                    "                round(CAST(t.value AS numeric), 2)                                      AS value_rigettato, " +
                    "                t.revenue_id                                                            AS revenueid, " +
                    "                0                                                                       AS revenue_value, " +
                    "                trf.revenue                                                             AS revenue_value_rigettato, " +
                    "                0                                                                       AS revenue, " +
                    "                round(CAST(trf.revenue AS numeric) * CAST(t.lead_number AS numeric), 2) AS revenue_rigettato, " +
                    "                0                                                                       AS click_number, " +
                    "                0                                                                       AS click_number_rigettato, " +
                    "                0                                                                       AS impression_number, " +
                    "                0                                                                       AS lead_number, " +
                    "                t.lead_number                                                           AS lead_number_rigettato, " +
                    "                t.data                                                                  AS data, " +
                    "                CAST(t.wallet_id as bigint)                                             AS walletid, " +
                    "                t.payout_present                                                        AS payoutpresent, " +
                    "                CAST(t.payout_id as bigint)                                             AS payoutid, " +
                    "                t.payout_reference                                                      AS payoutreference " +
                    "FROM t_transaction_cpl t " +
                    "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                    "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
                    "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                    "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                    "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
                    "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                    "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                    "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                    "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                    "WHERE (t.status_id = 74 " +
                    "    OR t.status_id = 70) " +
                    "  AND (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
                    "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
                    "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                    "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                    "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                    "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                    "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                    "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
                    "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
                    "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
                    "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
                    "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
                    "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                    "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
                    "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
                    "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
                    "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                    "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))) ;"
    )
    List<QueryTransaction> listaTransazioniCPL(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("statusId") Long statusId,
            @Param("dictionaryId") Long dictionaryId,
            @Param("affiliateId") Long affiliateId,
            @Param("channelId") Long channelId,
            @Param("campaignId") Long campaignId,
            @Param("mediaId") Long mediaId,
            @Param("commissionId") Long commissionId,
            @Param("revenueId") Long revenueId,
            @Param("payoutPresent") Boolean payoutPresent,
            @Param("payoutId") Long payoutId,
            @Param("advertiserId") Long advertiserId,
            @Param("valueNotZero") Boolean valueNotZero,
            @Param("inDictionaryId") List<Long> inDictionaryId,
            @Param("notInDictionaryId") List<Long> notInDictionaryId,
            @Param("inStausId") List<Long> inStausId,
            @Param("notInStausId") List<Long> notInStausId
    );

    //=========================================================================================================================
    // CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM
    //=========================================================================================================================

    @Query(nativeQuery = true, value =
            "SELECT DISTINCT row_number() OVER ()                                                          AS rn, " +
                    "                CAST('CPM' AS text)                                                           AS tipo, " +
                    "                CAST(t.id as bigint)                                                          AS id, " +
                    "                t.creation_date                                                               AS creationdate, " +
                    "                date(t.date_time)                                                             AS datetime, " +
                    "                CAST(t.status_id as bigint)                                                   AS statusid, " +
                    "                tdd.name                                                                      AS statusname, " +
                    "                CAST(t.dictionary_id as bigint)                                               AS dictionaryid, " +
                    "                td.name                                                                       AS dictionaryname, " +
                    "                CAST(t.affiliate_id as bigint)                                                AS affiliateid, " +
                    "                ta.name                                                                       AS affiliatename, " +
                    "                CAST(tadv.id as bigint)                                                       AS advertiserid, " +
                    "                tadv.name                                                                     AS advertisername, " +
                    "                CAST(t.channel_id as bigint)                                                  AS channelid, " +
                    "                c.name                                                                        AS channelname, " +
                    "                CAST(t.campaign_id as bigint)                                                 AS campaignid, " +
                    "                tc.name                                                                       AS campaignname, " +
                    "                CAST(t.media_id as bigint)                                                    AS mediaid, " +
                    "                tm.name                                                                       AS medianame, " +
                    "                CAST(t.commission_id as bigint)                                               AS commissionid, " +
                    "                tco.name                                                                      AS commissionname, " +
                    "                tco.value                                                                     AS commission_value, " +
                    "                0                                                                             AS commission_value_rigettato, " +
                    "                round(CAST(t.value AS numeric), 2)                                            AS value, " +
                    "                0                                                                             AS value_rigettato, " +
                    "                t.revenue_id                                                                  AS revenueid, " +
                    "                trf.revenue                                                                   AS revenue_value, " +
                    "                0                                                                             AS revenue_value_rigettato, " +
                    "                round(CAST(trf.revenue AS numeric) * CAST(t.impression_number AS numeric), 2) AS revenue, " +
                    "                0                                                                             AS revenue_rigettato, " +
                    "                0                                                                             AS click_number, " +
                    "                0                                                                             AS click_number_rigettato, " +
                    "                t.impression_number                                                           AS impressionnumber, " +
                    "                0                                                                             AS lead_number, " +
                    "                0                                                                             AS lead_number_rigettato, " +
                    "                CAST(NULL AS character varying)                                               AS data, " +
                    "                CAST(t.wallet_id as bigint)                                                   AS walletid, " +
                    "                t.payout_present                                                              AS payoutpresent, " +
                    "                CAST(t.payout_id as bigint)                                                   AS payoutid, " +
                    "                t.payout_reference                                                            AS payoutreference " +
                    "FROM t_transaction_cpm t " +
                    "         LEFT JOIN t_campaign tc ON t.campaign_id = tc.id " +
                    "         LEFT JOIN t_media tm ON t.media_id = tm.id " +
                    "         LEFT JOIN t_affiliate ta ON t.affiliate_id = ta.id " +
                    "         LEFT JOIN t_commision tco ON t.commission_id = tco.id " +
                    "         LEFT JOIN t_channel c ON t.channel_id = c.id " +
                    "         LEFT JOIN t_dictionary td ON t.dictionary_id = td.id " +
                    "         LEFT JOIN t_dictionary tdd ON t.status_id = tdd.id " +
                    "         LEFT JOIN t_revenuefactor trf ON t.revenue_id = trf.id " +
                    "         LEFT JOIN t_advertiser tadv ON tc.advertiser_id = tadv.id " +
                    "WHERE (cast(:dateFrom as date) IS NULL OR (:dateFrom <= t.date_time)) " +
                    "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= t.date_time)) " +
                    "  AND ((:statusId) IS NULL OR (t.status_id = CAST(:statusId as bigint))) " +
                    "  AND ((:dictionaryId) IS NULL OR (t.dictionary_id = CAST(:dictionaryId as bigint))) " +
                    "  AND ((:affiliateId) IS NULL OR (t.affiliate_id = CAST(:affiliateId as bigint))) " +
                    "  AND ((:channelId) IS NULL OR (t.channel_id = CAST(:channelId as bigint))) " +
                    "  AND ((:campaignId) IS NULL OR (t.campaign_id = CAST(:campaignId as bigint))) " +
                    "  AND ((:mediaId) IS NULL OR (t.media_id = CAST(:mediaId as bigint))) " +
                    "  AND ((:commissionId) IS NULL OR (t.commission_id = CAST(:commissionId as bigint))) " +
                    "  AND ((:revenueId) IS NULL OR (t.revenue_id = CAST(:revenueId as bigint))) " +
                    "  AND ((:payoutPresent) IS NULL OR (t.payout_present = (:payoutPresent))) " +
                    "  AND ((:payoutId) IS NULL OR (t.payout_id = CAST(:payoutId as bigint))) " +
                    "  AND ((:advertiserId) IS NULL OR (tc.advertiser_id = CAST(:advertiserId as bigint))) " +
                    "  AND ((:valueNotZero) IS NULL OR (t.value <> 0)) " +
                    "  AND (CAST((:inDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id in (:inDictionaryId))) " +
                    "  AND (CAST((:notInDictionaryId) as bigint[]) IS NULL OR (t.dictionary_id not in (:notInDictionaryId))) " +
                    "  AND (CAST((:inStausId) as bigint[]) IS NULL OR (t.status_id in (:inStausId))) " +
                    "  AND (CAST((:notInStausId) as bigint[]) IS NULL OR (t.status_id not in (:notInStausId))); ;"
    )
    List<QueryTransaction> listaTransazioniCPM(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("statusId") Long statusId,
            @Param("dictionaryId") Long dictionaryId,
            @Param("affiliateId") Long affiliateId,
            @Param("channelId") Long channelId,
            @Param("campaignId") Long campaignId,
            @Param("mediaId") Long mediaId,
            @Param("commissionId") Long commissionId,
            @Param("revenueId") Long revenueId,
            @Param("payoutPresent") Boolean payoutPresent,
            @Param("payoutId") Long payoutId,
            @Param("advertiserId") Long advertiserId,
            @Param("valueNotZero") Boolean valueNotZero,
            @Param("inDictionaryId") List<Long> inDictionaryId,
            @Param("notInDictionaryId") List<Long> notInDictionaryId,
            @Param("inStausId") List<Long> inStausId,
            @Param("notInStausId") List<Long> notInStausId
    );

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================r

}