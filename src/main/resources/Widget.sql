create or replace view public.v_widget_cpc
as
SELECT al.campaign_id                                  as campaignId,
       al.campaign_name                                as campaignName,
       al.affiliate_id                                 as affiliateId,
       al.affiliate_name                               as affiliateName,
       al.channel_id                                   as channelId,
       al.channel_name                                 as channelName,
       al.media_id                                     as mediaID,
       al.media_name                                   as mediaName,
       al.dictionary_id                                as dictionaryId,
       al.date_time                                    as date,
       round(al.value::numeric, 2)                     as commssion,
       sum(al.click_number)                            as clickNumber,
       tr.revenue * al.click_number                    as revenue,
       round((al.value / al.click_number)::numeric, 2) as ecpc
from v_transactions_all al
         inner join t_revenuefactor tr on al.revenue_id = tr.id
where al.tipo = 'CPC'
  and tr.id = al.revenue_id
group by al.campaign_name, al.campaign_id, tr.revenue, al.value, al.click_number, al.affiliate_id, al.affiliate_name,
         al.channel_id, al.channel_name, al.dictionary_id, al.date_time, al.media_id, al.media_name
order by al.campaign_id;

-------------------------------------------------------------
-------------------------------------------------------------

create or replace view public.v_widget_cpm
as
SELECT al.campaign_id                                         as campaignId,
       al.campaign_name                                       as campaignName,
       al.affiliate_id                                        as affiliateId,
       al.affiliate_name                                      as affiliateName,
       al.channel_id                                          as channelId,
       al.channel_name                                        as channelName,
       al.media_id                                            as mediaId,
       al.media_name                                          as mediaName,
       al.dictionary_id                                       as dictionaryId,
       al.date_time                                           as date,
       round(al.value::numeric, 2)                            as commssion,
       (impression_number)                                    as impression,
       ((al.value) / (impression_number)) * 1000              as ecpm,
       round((tr.revenue * al.impression_number)::numeric, 2) as revenue
from v_transactions_all al
         inner join t_revenuefactor tr on al.campaign_id = tr.campaign_id
where al.tipo = 'CPM'
  and tr.dictionary_id = 50
group by al.campaign_name, al.campaign_id, tr.revenue, al.value, impression_number, al.affiliate_id, al.affiliate_name,
         al.channel_id, al.channel_name, al.dictionary_id, al.date_time, al.media_id, al.media_name
order by al.campaign_id;

-------------------------------------------------------------
-------------------------------------------------------------

create or replace view public.v_widget_cpl
as
SELECT al.campaign_id                                   as campaignId,
       al.campaign_name                                 as campaignName,
       al.affiliate_id                                  as affiliateId,
       al.affiliate_name                                as affiliateName,
       al.channel_id                                    as channelId,
       al.channel_name                                  as channelName,
       al.media_id                                      as mediaId,
       al.media_name                                    as mediaName,
       al.dictionary_id                                 as dictionaryId,
       al.date_time                                     as date,
       round(al.value::numeric, 2)                      as commssion,
       lead_number                                      as leadNumber,
       al.value / lead_number                           as ecpl,
       round((tr.revenue * al.lead_number)::numeric, 2) as revenue
from v_transactions_all al
         inner join t_revenuefactor tr on al.campaign_id = tr.campaign_id
where al.tipo = 'CPL'
  and tr.dictionary_id = 11
group by al.campaign_name, al.campaign_id, tr.revenue, al.value, al.lead_number, al.affiliate_id, al.affiliate_name,
         al.channel_id, al.channel_name, al.dictionary_id, al.date_time, al.media_name, al.media_id
order by al.campaign_id;



---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------------------------------------------


create or replace view public.v_widget_campaign_day_cpc as
SELECT row_number() OVER ()                   AS id,
       tt.click_number                        AS totale,
       tt.value                               AS valore,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.affiliate_id,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('week'::text, tt.date_time)  AS week,
       date_part('doy'::text, tt.date_time)   AS doy,
       tt.date_time
FROM t_transaction_cpc tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
GROUP BY tt.click_number, tt.value, tc.name, tt.campaign_id, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('doy'::text, tt.date_time)), (date_part('week'::text, tt.date_time)), tt.date_time, tt.affiliate_id
ORDER BY tt.campaign_id;

alter table public.v_widget_campaign_day_cpc
    owner to postgres;

-----------------------------------------------
-----------------------------------------------
-----------------------------------------------
-----------------------------------------------
-----------------------------------------------

create view public.v_widget_campaign_day_cpm as
SELECT row_number() OVER ()                   AS id,
       tt.impression_number                   AS totale,
       tt.value                               AS valore,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.affiliate_id,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('doy'::text, tt.date_time)   AS doy,
       date_part('week'::text, tt.date_time)  AS week
FROM t_transaction_cpm tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.impression_number, tt.value, tc.name, tt.campaign_id, tm.name, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('week'::text, tt.date_time)), (date_part('doy'::text, tt.date_time)), tt.affiliate_id
ORDER BY tt.campaign_id;

alter table public.v_widget_campaign_day_cpm
    owner to postgres;

-----------------------------------------------
-----------------------------------------------
-----------------------------------------------
-----------------------------------------------
-----------------------------------------------

create view public.v_widget_campaign_day_cpl as
SELECT row_number() OVER ()                   AS id,
       tt.value                               AS valore,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.affiliate_id,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('doy'::text, tt.date_time)   AS doy,
       date_part('week'::text, tt.date_time)  AS week
FROM t_transaction_cpl tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.value, tc.name, tt.campaign_id, tm.name, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('week'::text, tt.date_time)), (date_part('doy'::text, tt.date_time)), tt.affiliate_id
ORDER BY tt.campaign_id;

alter table public.v_widget_campaign_day_cpl
    owner to postgres;

-----------------------------------------------
-----------------------------------------------
-----------------------------------------------
-----------------------------------------------
-----------------------------------------------

create view public.v_widget_campaign_day_cps as
SELECT row_number() OVER ()                   AS id,
       tt.value                               AS valore,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.affiliate_id,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('doy'::text, tt.date_time)   AS doy,
       date_part('week'::text, tt.date_time)  AS week
FROM t_transaction_cps tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.value, tc.name, tt.campaign_id, tm.name, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('week'::text, tt.date_time)), (date_part('doy'::text, tt.date_time)), tt.affiliate_id
ORDER BY tt.campaign_id;

alter table public.v_widget_campaign_day_cps
    owner to postgres;

