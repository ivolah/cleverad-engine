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
