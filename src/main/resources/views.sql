create
or replace view public.v_transactions_all
            (rn, id, agent, approved, creation_date, date_time, ip, note, payout_reference, value, affiliate_id,
             affiliate_name, campaign_id, campaign_name, channel_id, channel_name, commission_id, commission_name,
             payout_id, wallet_id, media_id, media_name, click_number, refferal, company_id, advertiser_id, data, tipo)
as
SELECT row_number() OVER ()    AS rn, t_transaction_cpc.id,
       t_transaction_cpc.agent,
       t_transaction_cpc.approved,
       t_transaction_cpc.creation_date,
       t_transaction_cpc.date_time,
       t_transaction_cpc.ip,
       t_transaction_cpc.note,
       t_transaction_cpc.payout_reference,
       t_transaction_cpc.value,
       t_transaction_cpc.affiliate_id,
       ta.name AS   affiliate_name,
       t_transaction_cpc.campaign_id,
       tc.name AS   campaign_name,
       t_transaction_cpc.channel_id,
       c.name  AS   channel_name,
       t_transaction_cpc.commission_id,
       t.name  AS   commission_name,
       t_transaction_cpc.payout_id,
       t_transaction_cpc.wallet_id,
       t_transaction_cpc.media_id,
       tm.name AS   media_name,
       t_transaction_cpc.click_number,
       NULL::character varying AS refferal,
       NULL::bigint            AS company_id,
       NULL::bigint            AS advertiser_id,
       NULL::character varying AS data,
       'CPC'::text             AS tipo
FROM t_transaction_cpc
    LEFT JOIN t_campaign tc
ON t_transaction_cpc.campaign_id = tc.id
    LEFT JOIN t_media tm ON t_transaction_cpc.media_id = tm.id
    LEFT JOIN t_affiliate ta ON t_transaction_cpc.affiliate_id = ta.id
    LEFT JOIN t_commision t ON t_transaction_cpc.commission_id = t.id
    LEFT JOIN t_channel c ON t_transaction_cpc.channel_id = c.id
UNION
SELECT row_number() OVER () AS rn, t_transaction_cpl.id,
       t_transaction_cpl.agent,
       t_transaction_cpl.approved,
       t_transaction_cpl.creation_date,
       t_transaction_cpl.date_time,
       t_transaction_cpl.ip,
       t_transaction_cpl.note,
       t_transaction_cpl.payout_reference,
       t_transaction_cpl.value,
       t_transaction_cpl.affiliate_id,
       ta.name AS   affiliate_name,
       t_transaction_cpl.campaign_id,
       tc.name AS   campaign_name,
       t_transaction_cpl.channel_id,
       c.name  AS   channel_name,
       t_transaction_cpl.commission_id,
       t.name  AS   commission_name,
       t_transaction_cpl.payout_id,
       t_transaction_cpl.wallet_id,
       t_transaction_cpl.media_id,
       tm.name AS   media_name,
       NULL::bigint         AS click_number,
       t_transaction_cpl.refferal,
       t_transaction_cpl.company_id,
       t_transaction_cpl.advertiser_id,
       t_transaction_cpl.data,
       'CPL'::text          AS tipo
FROM t_transaction_cpl
         LEFT JOIN t_campaign tc ON t_transaction_cpl.campaign_id = tc.id
         LEFT JOIN t_media tm ON t_transaction_cpl.media_id = tm.id
         LEFT JOIN t_affiliate ta ON t_transaction_cpl.affiliate_id = ta.id
         LEFT JOIN t_commision t ON t_transaction_cpl.commission_id = t.id
         LEFT JOIN t_channel c ON t_transaction_cpl.channel_id = c.id
SELECT row_number() OVER () AS rn, t_transaction_cps.id,
       t_transaction_cps.agent,
       t_transaction_cps.approved,
       t_transaction_cps.creation_date,
       t_transaction_cps.date_time,
       t_transaction_cps.ip,
       t_transaction_cps.note,
       t_transaction_cps.payout_reference,
       t_transaction_cps.value,
       t_transaction_cps.affiliate_id,
       ta.name AS   affiliate_name,
       t_transaction_cps.campaign_id,
       tc.name AS   campaign_name,
       t_transaction_cps.channel_id,
       c.name  AS   channel_name,
       t_transaction_cps.commission_id,
       t.name  AS   commission_name,
       t_transaction_cps.payout_id,
       t_transaction_cps.wallet_id,
       t_transaction_cps.media_id,
       tm.name AS   media_name,
       NULL::bigint         AS click_number,
       t_transaction_cps.refferal,
       t_transaction_cps.company_id,
       t_transaction_cps.advertiser_id,
       t_transaction_cps.data,
       'CPS'::text          AS tipo
FROM t_transaction_cpl
         LEFT JOIN t_campaign tc ON t_transaction_cps.campaign_id = tc.id
         LEFT JOIN t_media tm ON t_transaction_cps.media_id = tm.id
         LEFT JOIN t_affiliate ta ON t_transaction_cps.affiliate_id = ta.id
         LEFT JOIN t_commision t ON t_transaction_cps.commission_id = t.id
         LEFT JOIN t_channel c ON t_transaction_cps.channel_id = c.id;

alter table public.v_transactions_all
    owner to postgres;

