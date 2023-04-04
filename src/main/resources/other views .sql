create or replace view public.v_cpc_click_campaign_media(id, click, campaign_id, campaign, media_id, media, file_id) as
SELECT row_number() OVER () AS id,
       sum(tt.click_number) AS click,
       tt.campaign_id,
       tc.name              AS campaign,
       tt.media_id,
       tm.name              AS media,
       tm.id_file           AS file_id
FROM t_transaction_cpc tt
         LEFT JOIN t_campaign tc ON (- tt.campaign_id) = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.campaign_id, tc.name, tt.media_id, tm.name, tm.id_file
ORDER BY tt.campaign_id;

alter table public.v_cpc_click_campaign_media
    owner to postgres;

create or replace view public.v_cpc_click_campaign_media_day
            (id, click, campaign_id, campaign, media_id, media, file_id, year, month, day, week) as
SELECT row_number() OVER ()                   AS id,
       sum(tt.click_number)                   AS click,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.media_id,
       tm.name                                AS media,
       tm.id_file                             AS file_id,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('week'::text, tt.date_time)  AS week
FROM t_transaction_cpc tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.campaign_id, tc.name, tt.media_id, tm.name, tm.id_file, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('week'::text, tt.date_time))
ORDER BY tt.campaign_id;

alter table public.v_cpc_click_campaign_media_day
    owner to postgres;

create or replace view public.v_cpc_transaction_campaign_media(id, totale, campaign_id, campaign, media_id, media, file_id) as
SELECT row_number() OVER () AS id,
       count(*)             AS totale,
       tt.campaign_id,
       tc.name              AS campaign,
       tt.media_id,
       tm.name              AS media,
       tm.id_file           AS file_id
FROM t_transaction_cpc tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tc.name, tt.campaign_id, tt.media_id, tm.name, tm.id_file
ORDER BY tt.campaign_id;

alter table public.v_cpc_transaction_campaign_media
    owner to postgres;

create or replace view public.v_cpc_transaction_campaign_media_day
            (id, totale, campaign_id, campaign, media_id, media, file_id, year, month, day, week) as
SELECT row_number() OVER ()                   AS id,
       count(*)                               AS totale,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.media_id,
       tm.name                                AS media,
       tm.id_file                             AS file_id,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('week'::text, tt.date_time)  AS week
FROM t_transaction_cpc tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tc.name, tt.campaign_id, tt.media_id, tm.name, tm.id_file, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('week'::text, tt.date_time))
ORDER BY tt.campaign_id;

alter table public.v_cpc_transaction_campaign_media_day
    owner to postgres;

create or replace view public.v_cpc_value_campaign_media(id, value, campaign_id, campaign, media_id, media, file_id) as
SELECT row_number() OVER () AS id,
       sum(tt.value)        AS value,
       tt.campaign_id,
       tc.name              AS campaign,
       tt.media_id,
       tm.name              AS media,
       tm.id_file           AS file_id
FROM t_transaction_cpc tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.campaign_id, tc.name, tt.media_id, tm.name, tm.id_file
ORDER BY tt.campaign_id;

alter table public.v_cpc_value_campaign_media
    owner to postgres;

create or replace view public.v_cpc_value_campaign_media_day
            (id, value, campaign_id, campaign, media_id, media, file_id, year, month, day, week) as
SELECT row_number() OVER ()                   AS id,
       sum(tt.value)                          AS value,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.media_id,
       tm.name                                AS media,
       tm.id_file                             AS file_id,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('week'::text, tt.date_time)  AS week
FROM t_transaction_cpc tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.campaign_id, tc.name, tt.media_id, tm.name, tm.id_file, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('week'::text, tt.date_time))
ORDER BY tt.campaign_id;

alter table public.v_cpc_value_campaign_media_day
    owner to postgres;

create or replace view public.v_cpl_value_campaign_media(id, value, campaign_id, campaign, media_id, media, file_id) as
SELECT row_number() OVER () AS id,
       sum(tt.value)        AS value,
       tt.campaign_id,
       tc.name              AS campaign,
       tt.media_id,
       tm.name              AS media,
       tm.id_file           AS file_id
FROM t_transaction_cpl tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.campaign_id, tc.name, tt.media_id, tm.name, tm.id_file
ORDER BY tt.campaign_id;

alter table public.v_cpl_value_campaign_media
    owner to postgres;

create or replace view public.v_cpl_value_campaign_media_day
            (id, value, campaign_id, campaign, media_id, media, file_id, year, month, day, week) as
SELECT row_number() OVER ()                   AS id,
       sum(tt.value)                          AS value,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.media_id,
       tm.name                                AS media,
       tm.id_file                             AS file_id,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('week'::text, tt.date_time)  AS week
FROM t_transaction_cpl tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_media tm ON tt.media_id = tm.id
GROUP BY tt.campaign_id, tc.name, tt.media_id, tm.name, tm.id_file, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('week'::text, tt.date_time))
ORDER BY tt.campaign_id;

alter table public.v_cpl_value_campaign_media_day
    owner to postgres;

create or replace view public.v_cpl_value_campaign_affiliate(id, value, campaign_id, campaign, affiliate_id, affiliate) as
SELECT row_number() OVER () AS id,
       sum(tt.value)        AS value,
       tt.campaign_id,
       tc.name              AS campaign,
       tt.affiliate_id,
       ta.name              AS affiliate
FROM t_transaction_cpl tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_affiliate ta ON tt.affiliate_id = ta.id
GROUP BY tt.campaign_id, tc.name, tt.affiliate_id, ta.name
ORDER BY tt.campaign_id;

alter table public.v_cpl_value_campaign_affiliate
    owner to postgres;

create or replace view public.v_cpl_value_campaign_affiliate_month
            (id, value, campaign_id, campaign, affiliate_id, affiliate, year, month) as
SELECT row_number() OVER ()                   AS id,
       sum(tt.value)                          AS value,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.affiliate_id,
       ta.name                                AS affiliate,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month
FROM t_transaction_cpl tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_affiliate ta ON tt.affiliate_id = ta.id
GROUP BY tt.campaign_id, tc.name, tt.affiliate_id, ta.name, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time))
ORDER BY tt.campaign_id;

alter table public.v_cpl_value_campaign_affiliate_month
    owner to postgres;

create or replace view public.v_cpl_value_campaign_affiliate_day
            (id, value, campaign_id, campaign, affiliate_id, affiliate, year, month, day, week) as
SELECT row_number() OVER ()                   AS id,
       sum(tt.value)                          AS value,
       tt.campaign_id,
       tc.name                                AS campaign,
       tt.affiliate_id,
       ta.name                                AS affiliate,
       date_part('year'::text, tt.date_time)  AS year,
       date_part('month'::text, tt.date_time) AS month,
       date_part('day'::text, tt.date_time)   AS day,
       date_part('week'::text, tt.date_time)  AS week
FROM t_transaction_cpl tt
         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id
         LEFT JOIN t_affiliate ta ON tt.affiliate_id = ta.id
GROUP BY tt.campaign_id, tc.name, tt.affiliate_id, ta.name, (date_part('year'::text, tt.date_time)),
         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)),
         (date_part('week'::text, tt.date_time))
ORDER BY tt.campaign_id;

alter table public.v_cpl_value_campaign_affiliate_day
    owner to postgres;

