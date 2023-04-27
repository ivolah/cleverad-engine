create or replace view public.v_transactions_all
            (rn, id, agent, approved, creation_date, date_time, ip, note, payout_reference, value, affiliate_id,
             affiliate_name, campaign_id, campaign_name, channel_id, channel_name, commission_id, commission_name,
             commission_value, payout_id, wallet_id, media_id, media_name, click_number, impression_number, lead_number,
             refferal, company_id, advertiser_id, data, dictionary_id, dictionary_name, budget, tipo, revenue_id)
as
SELECT DISTINCT row_number() OVER ()                       AS rn,
                t_transaction_cpc.id,
                t_transaction_cpc.agent,
                t_transaction_cpc.approved,
                t_transaction_cpc.creation_date,
                t_transaction_cpc.date_time,
                t_transaction_cpc.ip,
                t_transaction_cpc.note,
                t_transaction_cpc.payout_reference,
                round(t_transaction_cpc.value::numeric, 2) AS value,
                t_transaction_cpc.affiliate_id,
                ta.name                                    AS affiliate_name,
                t_transaction_cpc.campaign_id,
                tc.name                                    AS campaign_name,
                t_transaction_cpc.channel_id,
                c.name                                     AS channel_name,
                t_transaction_cpc.commission_id,
                t.name                                     AS commission_name,
                t.value                                    AS commission_value,
                t_transaction_cpc.payout_id,
                t_transaction_cpc.wallet_id,
                t_transaction_cpc.media_id,
                tm.name                                    AS media_name,
                t_transaction_cpc.click_number,
                NULL::bigint                               AS impression_number,
                NULL::bigint                               AS lead_number,
                NULL::character varying                    AS refferal,
                NULL::bigint                               AS company_id,
                NULL::bigint                               AS advertiser_id,
                NULL::character varying                    AS data,
                t_transaction_cpc.dictionary_id,
                td.name                                    AS dictionary_name,
                tc.budget,
                'CPC'::text                                AS tipo,
                t_transaction_cpc.revenue_id
FROM t_transaction_cpc
         LEFT JOIN t_campaign tc ON t_transaction_cpc.campaign_id = tc.id
         LEFT JOIN t_media tm ON t_transaction_cpc.media_id = tm.id
         LEFT JOIN t_affiliate ta ON t_transaction_cpc.affiliate_id = ta.id
         LEFT JOIN t_commision t ON t_transaction_cpc.commission_id = t.id
         LEFT JOIN t_channel c ON t_transaction_cpc.channel_id = c.id
         LEFT JOIN t_dictionary td ON t_transaction_cpc.dictionary_id = td.id
UNION
SELECT DISTINCT row_number() OVER ()                       AS rn,
                t_transaction_cpl.id,
                t_transaction_cpl.agent,
                t_transaction_cpl.approved,
                t_transaction_cpl.creation_date,
                t_transaction_cpl.date_time,
                t_transaction_cpl.ip,
                t_transaction_cpl.note,
                t_transaction_cpl.payout_reference,
                round(t_transaction_cpl.value::numeric, 2) AS value,
                t_transaction_cpl.affiliate_id,
                ta.name                                    AS affiliate_name,
                t_transaction_cpl.campaign_id,
                tc.name                                    AS campaign_name,
                t_transaction_cpl.channel_id,
                c.name                                     AS channel_name,
                t_transaction_cpl.commission_id,
                t.name                                     AS commission_name,
                t.value                                    AS commission_value,
                t_transaction_cpl.payout_id,
                t_transaction_cpl.wallet_id,
                t_transaction_cpl.media_id,
                tm.name                                    AS media_name,
                NULL::bigint                               AS click_number,
                NULL::bigint                               AS impression_number,
                t_transaction_cpl.lead_number,
                t_transaction_cpl.refferal,
                t_transaction_cpl.company_id,
                t_transaction_cpl.advertiser_id,
                t_transaction_cpl.data,
                t_transaction_cpl.dictionary_id,
                td.name                                    AS dictionary_name,
                tc.budget,
                'CPL'::text                                AS tipo,
                t_transaction_cpl.revenue_id
FROM t_transaction_cpl
         LEFT JOIN t_campaign tc ON t_transaction_cpl.campaign_id = tc.id
         LEFT JOIN t_media tm ON t_transaction_cpl.media_id = tm.id
         LEFT JOIN t_affiliate ta ON t_transaction_cpl.affiliate_id = ta.id
         LEFT JOIN t_commision t ON t_transaction_cpl.commission_id = t.id
         LEFT JOIN t_channel c ON t_transaction_cpl.channel_id = c.id
         LEFT JOIN t_dictionary td ON t_transaction_cpl.dictionary_id = td.id
UNION
SELECT DISTINCT row_number() OVER ()    AS rn,
                t_transaction_cpm.id,
                t_transaction_cpm.agent,
                t_transaction_cpm.approved,
                t_transaction_cpm.creation_date,
                t_transaction_cpm.date_time,
                t_transaction_cpm.ip,
                t_transaction_cpm.note,
                t_transaction_cpm.payout_reference,
                t_transaction_cpm.value,
                t_transaction_cpm.affiliate_id,
                ta.name                 AS affiliate_name,
                t_transaction_cpm.campaign_id,
                tc.name                 AS campaign_name,
                t_transaction_cpm.channel_id,
                c.name                  AS channel_name,
                t_transaction_cpm.commission_id,
                t.name                  AS commission_name,
                t.value                 AS commission_value,
                t_transaction_cpm.payout_id,
                t_transaction_cpm.wallet_id,
                t_transaction_cpm.media_id,
                tm.name                 AS media_name,
                NULL::bigint            AS click_number,
                t_transaction_cpm.impression_number,
                NULL::bigint            AS lead_number,
                NULL::character varying AS refferal,
                NULL::bigint            AS company_id,
                NULL::bigint            AS advertiser_id,
                t_transaction_cpm.data,
                t_transaction_cpm.dictionary_id,
                td.name                 AS dictionary_name,
                tc.budget,
                'CPM'::text             AS tipo,
                t_transaction_cpm.revenue_id
FROM t_transaction_cpm
         LEFT JOIN t_campaign tc ON t_transaction_cpm.campaign_id = tc.id
         LEFT JOIN t_media tm ON t_transaction_cpm.media_id = tm.id
         LEFT JOIN t_affiliate ta ON t_transaction_cpm.affiliate_id = ta.id
         LEFT JOIN t_commision t ON t_transaction_cpm.commission_id = t.id
         LEFT JOIN t_channel c ON t_transaction_cpm.channel_id = c.id
         LEFT JOIN t_dictionary td ON t_transaction_cpm.dictionary_id = td.id
UNION
SELECT DISTINCT row_number() OVER ()                       AS rn,
                t_transaction_cps.id,
                t_transaction_cps.agent,
                t_transaction_cps.approved,
                t_transaction_cps.creation_date,
                t_transaction_cps.date_time,
                t_transaction_cps.ip,
                t_transaction_cps.note,
                t_transaction_cps.payout_reference,
                round(t_transaction_cps.value::numeric, 2) AS value,
                t_transaction_cps.affiliate_id,
                ta.name                                    AS affiliate_name,
                t_transaction_cps.campaign_id,
                tc.name                                    AS campaign_name,
                t_transaction_cps.channel_id,
                c.name                                     AS channel_name,
                t_transaction_cps.commission_id,
                t.name                                     AS commission_name,
                t.value                                    AS commission_value,
                t_transaction_cps.payout_id,
                t_transaction_cps.wallet_id,
                t_transaction_cps.media_id,
                tm.name                                    AS media_name,
                NULL::bigint                               AS click_number,
                NULL::bigint                               AS impression_number,
                NULL::bigint                               AS lead_number,
                t_transaction_cps.refferal,
                t_transaction_cps.company_id,
                t_transaction_cps.advertiser_id,
                t_transaction_cps.data,
                t_transaction_cps.dictionary_id,
                td.name                                    AS dictionary_name,
                tc.budget,
                'CPS'::text                                AS tipo,
                t_transaction_cps.revenue_id
FROM t_transaction_cps
         LEFT JOIN t_campaign tc ON t_transaction_cps.campaign_id = tc.id
         LEFT JOIN t_media tm ON t_transaction_cps.media_id = tm.id
         LEFT JOIN t_affiliate ta ON t_transaction_cps.affiliate_id = ta.id
         LEFT JOIN t_commision t ON t_transaction_cps.commission_id = t.id
         LEFT JOIN t_channel c ON t_transaction_cps.channel_id = c.id
         LEFT JOIN t_dictionary td ON t_transaction_cps.dictionary_id = td.id;

alter table public.v_transactions_all
    owner to postgres;

