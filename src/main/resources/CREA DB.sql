create table public.t_advertiser (
                                     id bigint primary key not null default nextval('t_advertiser_id_seq'::regclass),
                                     city character varying(255),
                                     creation_date timestamp without time zone,
                                     last_modification_date timestamp without time zone,
                                     name character varying(255),
                                     primary_mail character varying(255),
                                     secondary_mail character varying(255),
                                     status boolean,
                                     street character varying(255),
                                     street_number character varying(255),
                                     vat_number character varying(255),
                                     zip_code character varying(255),
                                     country character varying
);

create table public.t_affiliate (
                                    id bigint primary key not null default nextval('t_affiliate_id_seq'::regclass),
                                    city character varying(255),
                                    creation_date date,
                                    last_modification_date date,
                                    name character varying(255),
                                    primary_mail character varying(255),
                                    secondary_mail character varying(255),
                                    street character varying(255),
                                    street_number character varying(255),
                                    vat_number character varying(30) not null,
                                    zip_code character varying(255),
                                    status boolean,
                                    iban character varying(255),
                                    paypal character varying(255),
                                    swift character varying(255),
                                    bank character varying(255),
                                    country character varying(255),
                                    note character varying(255),
                                    phone_number character varying(255),
                                    phone_prefix character varying(255),
                                    province character varying(255),
                                    contenuto_sito character varying(255),
                                    first_name character varying(255),
                                    last_name character varying(255),
                                    nome_sito_social character varying(255),
                                    url_sito_social character varying(255),
                                    channeltype_id bigint,
                                    companytype_id bigint,
                                    cb boolean,
                                    type integer,
                                    status_id bigint,
                                    foreign key (companytype_id) references public.t_dictionary (id)
                                        match simple on update no action on delete no action,
                                    foreign key (channeltype_id) references public.t_dictionary (id)
                                        match simple on update no action on delete no action,
                                    foreign key (status_id) references public.t_dictionary (id)
                                        match simple on update no action on delete no action
);

create table public.t_affiliate_budget_campaign (
                                                    id bigint primary key not null default nextval('t_affiliate_budget_campaign_id_seq'::regclass),
                                                    creation_date timestamp without time zone,
                                                    last_modification_date timestamp without time zone,
                                                    affiliate_id bigint,
                                                    budget_id bigint,
                                                    campaign_id bigint,
                                                    foreign key (budget_id) references public.t_budget (id)
                                                        match simple on update no action on delete no action,
                                                    foreign key (affiliate_id) references public.t_affiliate (id)
                                                        match simple on update no action on delete no action,
                                                    foreign key (campaign_id) references public.t_campaign (id)
                                                        match simple on update no action on delete no action
);

create table public.t_affiliate_channel_commission_campaign (
                                                                id bigint primary key not null default nextval('t_affiliate_channel_commission_campaign_id_seq'::regclass),
                                                                creation_date timestamp without time zone,
                                                                last_modification_date timestamp without time zone,
                                                                affiliate_id bigint,
                                                                campaign_id bigint,
                                                                channel_id bigint,
                                                                commission_id bigint,
                                                                foreign key (channel_id) references public.t_channel (id)
                                                                    match simple on update no action on delete no action,
                                                                foreign key (campaign_id) references public.t_campaign (id)
                                                                    match simple on update no action on delete no action,
                                                                foreign key (affiliate_id) references public.t_affiliate (id)
                                                                    match simple on update no action on delete no action,
                                                                foreign key (commission_id) references public.t_commision (id)
                                                                    match simple on update no action on delete no action
);

create table public.t_budget (
                                 id bigint primary key not null default nextval('t_budget_id_seq'::regclass),
                                 budget double precision,
                                 creation_date timestamp without time zone,
                                 due_date date,
                                 last_modification_date timestamp without time zone,
                                 status boolean not null,
                                 affiliate_id bigint,
                                 campaign_id bigint,
                                 start_date date,
                                 foreign key (campaign_id) references public.t_campaign (id)
                                     match simple on update no action on delete no action,
                                 foreign key (affiliate_id) references public.t_affiliate (id)
                                     match simple on update no action on delete no action
);

create table public.t_campaign (
                                   id bigint primary key not null default nextval('t_campaign_id_seq'::regclass),
                                   creation_date timestamp without time zone,
                                   end_date date,
                                   last_modification_date timestamp without time zone,
                                   long_description character varying,
                                   name character varying(255),
                                   short_description character varying(255),
                                   start_date date,
                                   default_commission_id character varying(255),
                                   id_file character varying(255),
                                   budget double precision,
                                   status boolean,
                                   valuta character varying(255),
                                   cookie_id bigint,
                                   tracking_code character varying(255),
                                   encoded_id character varying(255),
                                   advertiser_id bigint,
                                   dealer_id bigint,
                                   planner_id bigint,
                                   initial_budget double precision,
                                   note character varying,
                                   foreign key (dealer_id) references public.t_dealer (id)
                                       match simple on update no action on delete no action,
                                   foreign key (planner_id) references public.t_planner (id)
                                       match simple on update no action on delete no action,
                                   foreign key (advertiser_id) references public.t_advertiser (id)
                                       match simple on update no action on delete no action,
                                   foreign key (cookie_id) references public.t_cookie (id)
                                       match simple on update no action on delete no action
);

create table public.t_campaign_affiliate (
                                             id bigint primary key not null default nextval('t_campaign_affiliate_id_seq'::regclass),
                                             affiliate_id bigint,
                                             campaign_id bigint,
                                             follow_through character varying(255),
                                             status_id integer,
                                             foreign key (affiliate_id) references public.t_affiliate (id)
                                                 match simple on update no action on delete no action,
                                             foreign key (campaign_id) references public.t_campaign (id)
                                                 match simple on update no action on delete no action,
                                             foreign key (status_id) references public.t_dictionary (id)
                                                 match simple on update no action on delete no action
);

create table public.t_campaign_category (
                                            id bigint primary key not null default nextval('t_campaign_category_id_seq'::regclass),
                                            creation_date timestamp without time zone,
                                            last_modification_date timestamp without time zone,
                                            campaign_id bigint,
                                            category_id bigint,
                                            foreign key (category_id) references public.t_category (id)
                                                match simple on update no action on delete no action,
                                            foreign key (campaign_id) references public.t_campaign (id)
                                                match simple on update no action on delete no action
);

create table public.t_campaign_cookie (
                                          id bigint primary key not null default nextval('t_campaign_cookie_id_seq'::regclass),
                                          creation_date timestamp without time zone,
                                          last_modification_date timestamp without time zone,
                                          campaign_id bigint,
                                          cookie_id bigint,
                                          foreign key (cookie_id) references public.t_cookie (id)
                                              match simple on update no action on delete no action,
                                          foreign key (campaign_id) references public.t_campaign (id)
                                              match simple on update no action on delete no action
);

create table public.t_campaign_media (
                                         campaign_id bigint not null,
                                         media_id bigint not null,
                                         primary key (campaign_id, media_id),
                                         foreign key (media_id) references public.t_media (id)
                                             match simple on update no action on delete no action,
                                         foreign key (campaign_id) references public.t_campaign (id)
                                             match simple on update no action on delete no action
);

create table public.t_campaign_revenuefactor (
                                                 id bigint primary key not null default nextval('t_campaign_revenuefactor_id_seq'::regclass),
                                                 creation_date timestamp without time zone,
                                                 last_modification_date timestamp without time zone,
                                                 campaign_id bigint,
                                                 revenuefactor_id bigint,
                                                 foreign key (revenuefactor_id) references public.t_revenuefactor (id)
                                                     match simple on update no action on delete no action,
                                                 foreign key (campaign_id) references public.t_campaign (id)
                                                     match simple on update no action on delete no action
);

create table public.t_category (
                                   id bigint primary key not null default nextval('t_category_id_seq'::regclass),
                                   code character varying(255),
                                   creation_date timestamp without time zone,
                                   description character varying(255),
                                   last_modification_date timestamp without time zone,
                                   name character varying(255)
);

create table public.t_channel (
                                  id bigint primary key not null default nextval('t_channel_id_seq'::regclass),
                                  creation_date timestamp without time zone,
                                  last_modification_date timestamp without time zone,
                                  name character varying(255),
                                  short_description character varying(255),
                                  status boolean not null,
                                  url character varying(255),
                                  dictionary_id bigint,
                                  affiliate_id bigint,
                                  type_id bigint,
                                  dimension character varying,
                                  country character varying,
                                  owner_id bigint,
                                  foreign key (affiliate_id) references public.t_affiliate (id)
                                      match simple on update no action on delete no action,
                                  foreign key (dictionary_id) references public.t_dictionary (id)
                                      match simple on update no action on delete no action,
                                  foreign key (type_id) references public.t_dictionary (id)
                                      match simple on update no action on delete no action,
                                  foreign key (owner_id) references public.t_dictionary (id)
                                      match simple on update no action on delete no action
);

create table public.t_channel_category (
                                           id bigint primary key not null default nextval('t_channel_category_id_seq'::regclass),
                                           creation_date timestamp without time zone,
                                           last_modification_date timestamp without time zone,
                                           category_id bigint,
                                           channel_id bigint,
                                           foreign key (category_id) references public.t_category (id)
                                               match simple on update no action on delete no action,
                                           foreign key (channel_id) references public.t_channel (id)
                                               match simple on update no action on delete no action
);

create table public.t_commision (
                                    id bigint primary key not null default nextval('t_commision_id_seq'::regclass),
                                    creation_date timestamp without time zone,
                                    description character varying(255),
                                    last_modification_date timestamp without time zone,
                                    name character varying(255),
                                    value character varying(255),
                                    due_date date,
                                    campaign_id bigint,
                                    dictionary_id bigint,
                                    status boolean,
                                    start_date date,
                                    base boolean default false,
                                    foreign key (campaign_id) references public.t_campaign (id)
                                        match simple on update no action on delete no action,
                                    foreign key (dictionary_id) references public.t_dictionary (id)
                                        match simple on update no action on delete no action
);

create table public.t_contact_form (
                                       id bigint primary key not null default nextval('t_contact_form_id_seq'::regclass),
                                       agree_data_procetction boolean,
                                       agree_mailing_list boolean,
                                       company_name character varying(255),
                                       country character varying(255),
                                       creation_date timestamp without time zone,
                                       email character varying(255),
                                       enquiry character varying(255),
                                       name character varying(255),
                                       phone_number character varying(255),
                                       request_type character varying(255),
                                       surname character varying(255)
);

create table public.t_cookie (
                                 id bigint primary key not null default nextval('t_cookie_id_seq'::regclass),
                                 creation_date timestamp without time zone,
                                 last_modification_date timestamp without time zone,
                                 name character varying(255),
                                 value character varying(255),
                                 status boolean
);

create table public.t_dealer (
                                 id bigint primary key not null default nextval('t_dealer_id_seq'::regclass),
                                 creation_date timestamp without time zone,
                                 email character varying(255),
                                 mobile character varying(255),
                                 mobile_prefix character varying(255),
                                 name character varying(255),
                                 phone character varying(255),
                                 phone_prefix character varying(255),
                                 status boolean not null,
                                 surname character varying(255)
);

create table public.t_dictionary (
                                     id bigint primary key not null default nextval('t_dictionary_id_seq'::regclass),
                                     description character varying(255),
                                     name character varying(255),
                                     status boolean not null,
                                     type character varying(255)
);

create table public.t_editor (
                                 id bigint primary key not null default nextval('t_editor_id_seq'::regclass),
                                 bank character varying(255),
                                 city character varying(255),
                                 country character varying(255),
                                 creation_date timestamp without time zone,
                                 iban character varying(255),
                                 last_modification_date timestamp without time zone,
                                 name character varying(255),
                                 note character varying(255),
                                 paypal character varying(255),
                                 phone_number character varying(255),
                                 phone_prefix character varying(255),
                                 primary_mail character varying(255),
                                 province character varying(255),
                                 secondary_mail character varying(255),
                                 status boolean not null,
                                 street character varying(255),
                                 street_number character varying(255),
                                 swift character varying(255),
                                 vat_number character varying(255),
                                 zip_code character varying(255)
);

create table public.t_file (
                               id bigint primary key not null default nextval('t_file_id_seq'::regclass),
                               creation_date timestamp without time zone,
                               data oid,
                               name character varying(255),
                               type character varying(255)
);

create table public.t_file_affiliate (
                                         id bigint primary key not null default nextval('t_file_affiliate_id_seq'::regclass),
                                         creation_date timestamp without time zone,
                                         data oid,
                                         doc_type character varying(255),
                                         name character varying(255),
                                         type character varying(255),
                                         affiliate_id bigint,
                                         note character varying(255),
                                         dictionary_id bigint,
                                         foreign key (dictionary_id) references public.t_dictionary (id)
                                             match simple on update no action on delete no action,
                                         foreign key (affiliate_id) references public.t_affiliate (id)
                                             match simple on update no action on delete no action
);

create table public.t_file_payout (
                                      id bigint primary key not null default nextval('t_file_payout_id_seq'::regclass),
                                      creation_date timestamp without time zone,
                                      data oid,
                                      doc_type character varying(255),
                                      name character varying(255),
                                      payout_id bigint,
                                      dictionary_id bigint,
                                      note character varying,
                                      type character varying,
                                      foreign key (dictionary_id) references public.t_dictionary (id)
                                          match simple on update no action on delete no action,
                                      foreign key (payout_id) references public.t_payout (id)
                                          match simple on update no action on delete no action
);

create table public.t_file_user (
                                    id bigint primary key not null default nextval('t_file_user_id_seq'::regclass),
                                    creation_date timestamp without time zone,
                                    data oid,
                                    type character varying(255),
                                    name character varying(255),
                                    user_id bigint,
                                    note character varying(255),
                                    avatar boolean
);

create table public.t_mail_template (
                                        id bigint primary key not null default nextval('t_mail_template_id_seq'::regclass),
                                        content character varying,
                                        creation_date timestamp without time zone,
                                        last_modification_date timestamp without time zone,
                                        name character varying(255),
                                        status boolean,
                                        subject character varying(255)
);

create table public.t_media (
                                id bigint primary key not null default nextval('t_media_id_seq'::regclass),
                                banner_code character varying,
                                creation_date timestamp without time zone,
                                id_file character varying,
                                last_modification_date timestamp without time zone,
                                name character varying(255),
                                note character varying,
                                status boolean,
                                target character varying,
                                url character varying,
                                type_id bigint,
                                mail_subject character varying,
                                foreign key (type_id) references public.t_media_type (id)
                                    match simple on update no action on delete no action
);

create table public.t_media_type (
                                     id bigint primary key not null default nextval('t_media_type_id_seq'::regclass),
                                     description character varying(255),
                                     name character varying(255),
                                     status boolean
);

create table public.t_payout (
                                 id bigint primary key not null default nextval('t_payout_id_seq'::regclass),
                                 affiliate_id bigint,
                                 creation_date timestamp without time zone,
                                 data date,
                                 last_modification_date timestamp without time zone,
                                 note character varying(255),
                                 stato boolean,
                                 totale double precision,
                                 valuta character varying(255),
                                 file_id bigint,
                                 dictionary_id bigint,
                                 foreign key (dictionary_id) references public.t_dictionary (id)
                                     match simple on update no action on delete no action,
                                 foreign key (affiliate_id) references public.t_affiliate (id)
                                     match simple on update no action on delete no action
);

create table public.t_planner (
                                  id bigint primary key not null default nextval('t_planner_id_seq'::regclass),
                                  creation_date timestamp without time zone,
                                  email character varying(255),
                                  mobile character varying(255),
                                  mobile_prefix character varying(255),
                                  name character varying(255),
                                  phone character varying(255),
                                  phone_prefix character varying(255),
                                  status boolean not null,
                                  surname character varying(255)
);

create table public.t_report (
                                 id bigint primary key not null default nextval('t_report_id_seq'::regclass),
                                 description character varying(255),
                                 name character varying(255),
                                 report_type_id bigint
);

create table public.t_representative (
                                         id bigint primary key not null default nextval('t_representative_id_seq'::regclass),
                                         creation_date timestamp without time zone,
                                         email character varying(255),
                                         mobile character varying(255),
                                         mobile_prefix character varying(255),
                                         name character varying(255),
                                         phone character varying(255),
                                         phone_prefix character varying(255),
                                         status boolean not null,
                                         surname character varying(255),
                                         affiliate_id bigint,
                                         role_id bigint,
                                         advertiser_id bigint,
                                         foreign key (role_id) references public.t_dictionary (id)
                                             match simple on update no action on delete no action,
                                         foreign key (advertiser_id) references public.t_advertiser (id)
                                             match simple on update no action on delete no action,
                                         foreign key (affiliate_id) references public.t_affiliate (id)
                                             match simple on update no action on delete no action
);

create table public.t_revenuefactor (
                                        id bigint primary key not null default nextval('t_revenuefactor_id_seq'::regclass),
                                        creation_date timestamp without time zone,
                                        due_date date,
                                        last_modification_date timestamp without time zone,
                                        revenue double precision,
                                        status boolean,
                                        campaign_id bigint,
                                        dictionary_id bigint,
                                        column_name integer,
                                        start_date date,
                                        foreign key (dictionary_id) references public.t_dictionary (id)
                                            match simple on update no action on delete no action,
                                        foreign key (campaign_id) references public.t_campaign (id)
                                            match simple on update no action on delete no action
);

create table public.t_target (
                                 id bigint primary key not null default nextval('t_target_id_seq'::regclass),
                                 target character varying,
                                 media_id bigint,
                                 foreign key (media_id) references public.t_media (id)
                                     match simple on update no action on delete no action
);

create table public.t_tracking (
                                   id bigint primary key not null default nextval('t_tracking_id_seq'::regclass),
                                   agent character varying(255),
                                   creation_date timestamp without time zone,
                                   ip character varying(255),
                                   refferal_id character varying(255),
                                   read boolean
);

create table public.t_transaction_cpc (
                                          id bigint primary key not null default nextval('t_transaction_cpc_id_seq'::regclass),
                                          agent character varying(255),
                                          approved boolean,
                                          click_number bigint,
                                          creation_date timestamp without time zone,
                                          date_time timestamp without time zone,
                                          ip character varying(255),
                                          last_modification_date timestamp without time zone,
                                          note character varying(255),
                                          payout_reference character varying(255),
                                          value double precision,
                                          affiliate_id bigint,
                                          campaign_id bigint,
                                          channel_id bigint,
                                          commission_id bigint,
                                          payout_id bigint,
                                          wallet_id bigint,
                                          media_id bigint,
                                          dictionary_id integer,
                                          revenue_id bigint,
                                          foreign key (payout_id) references public.t_payout (id)
                                              match simple on update no action on delete no action,
                                          foreign key (affiliate_id) references public.t_affiliate (id)
                                              match simple on update no action on delete no action,
                                          foreign key (wallet_id) references public.t_wallet (id)
                                              match simple on update no action on delete no action,
                                          foreign key (channel_id) references public.t_channel (id)
                                              match simple on update no action on delete no action,
                                          foreign key (media_id) references public.t_media (id)
                                              match simple on update no action on delete no action,
                                          foreign key (campaign_id) references public.t_campaign (id)
                                              match simple on update no action on delete no action,
                                          foreign key (commission_id) references public.t_commision (id)
                                              match simple on update no action on delete no action,
                                          foreign key (dictionary_id) references public.t_dictionary (id)
                                              match simple on update no action on delete no action
);

create table public.t_transaction_cpl (
                                          id bigint primary key not null default nextval('t_transaction_cpl_id_seq'::regclass),
                                          agent character varying(255),
                                          approved boolean,
                                          creation_date timestamp without time zone,
                                          data character varying(255),
                                          date_time timestamp without time zone,
                                          ip character varying(255),
                                          last_modification_date timestamp without time zone,
                                          note character varying(255),
                                          payout_reference character varying(255),
                                          refferal character varying(255),
                                          value double precision,
                                          affiliate_id bigint,
                                          campaign_id bigint,
                                          channel_id bigint,
                                          commission_id bigint,
                                          company_id bigint,
                                          payout_id bigint,
                                          media_id bigint,
                                          wallet_id bigint,
                                          advertiser_id bigint,
                                          dictionary_id integer,
                                          lead_number bigint,
                                          revenue_id bigint,
                                          foreign key (commission_id) references public.t_commision (id)
                                              match simple on update no action on delete no action,
                                          foreign key (channel_id) references public.t_channel (id)
                                              match simple on update no action on delete no action,
                                          foreign key (campaign_id) references public.t_campaign (id)
                                              match simple on update no action on delete no action,
                                          foreign key (wallet_id) references public.t_wallet (id)
                                              match simple on update no action on delete no action,
                                          foreign key (media_id) references public.t_media (id)
                                              match simple on update no action on delete no action,
                                          foreign key (affiliate_id) references public.t_affiliate (id)
                                              match simple on update no action on delete no action,
                                          foreign key (advertiser_id) references public.t_advertiser (id)
                                              match simple on update no action on delete no action,
                                          foreign key (company_id) references public.t_advertiser (id)
                                              match simple on update no action on delete no action,
                                          foreign key (payout_id) references public.t_payout (id)
                                              match simple on update no action on delete no action,
                                          foreign key (dictionary_id) references public.t_dictionary (id)
                                              match simple on update no action on delete no action
);

create table public.t_transaction_cpm (
                                          id bigint primary key not null default nextval('t_transaction_cpm_id_seq'::regclass),
                                          agent character varying(255),
                                          approved boolean,
                                          creation_date timestamp without time zone,
                                          data character varying(255),
                                          date_time timestamp without time zone,
                                          image_id bigint,
                                          ip character varying(255),
                                          last_modification_date timestamp without time zone,
                                          media_id bigint,
                                          note character varying(255),
                                          payout_reference character varying(255),
                                          type character varying(255),
                                          value double precision,
                                          affiliate_id bigint,
                                          campaign_id bigint,
                                          channel_id bigint,
                                          commission_id bigint,
                                          payout_id bigint,
                                          wallet_id bigint,
                                          dictionary_id integer,
                                          impression_number bigint,
                                          revenue_id bigint,
                                          foreign key (commission_id) references public.t_commision (id)
                                              match simple on update no action on delete no action,
                                          foreign key (wallet_id) references public.t_wallet (id)
                                              match simple on update no action on delete no action,
                                          foreign key (channel_id) references public.t_channel (id)
                                              match simple on update no action on delete no action,
                                          foreign key (campaign_id) references public.t_campaign (id)
                                              match simple on update no action on delete no action,
                                          foreign key (media_id) references public.t_media (id)
                                              match simple on update no action on delete no action,
                                          foreign key (affiliate_id) references public.t_affiliate (id)
                                              match simple on update no action on delete no action,
                                          foreign key (payout_id) references public.t_payout (id)
                                              match simple on update no action on delete no action,
                                          foreign key (dictionary_id) references public.t_dictionary (id)
                                              match simple on update no action on delete no action
);

create table public.t_transaction_cps (
                                          id bigint primary key not null default nextval('t_transaction_cps_id_seq'::regclass),
                                          agent character varying(255),
                                          approved boolean,
                                          creation_date timestamp without time zone,
                                          data character varying(255),
                                          date_time timestamp without time zone,
                                          ip character varying(255),
                                          last_modification_date timestamp without time zone,
                                          note character varying(255),
                                          payout_reference character varying(255),
                                          refferal character varying(255),
                                          value double precision,
                                          affiliate_id bigint,
                                          campaign_id bigint,
                                          channel_id bigint,
                                          commission_id bigint,
                                          company_id bigint,
                                          payout_id bigint,
                                          media_id bigint,
                                          wallet_id bigint,
                                          advertiser_id bigint,
                                          dictionary_id integer,
                                          revenue_id bigint,
                                          foreign key (commission_id) references public.t_commision (id)
                                              match simple on update no action on delete no action,
                                          foreign key (channel_id) references public.t_channel (id)
                                              match simple on update no action on delete no action,
                                          foreign key (campaign_id) references public.t_campaign (id)
                                              match simple on update no action on delete no action,
                                          foreign key (wallet_id) references public.t_wallet (id)
                                              match simple on update no action on delete no action,
                                          foreign key (media_id) references public.t_media (id)
                                              match simple on update no action on delete no action,
                                          foreign key (affiliate_id) references public.t_affiliate (id)
                                              match simple on update no action on delete no action,
                                          foreign key (advertiser_id) references public.t_advertiser (id)
                                              match simple on update no action on delete no action,
                                          foreign key (company_id) references public.t_advertiser (id)
                                              match simple on update no action on delete no action,
                                          foreign key (payout_id) references public.t_payout (id)
                                              match simple on update no action on delete no action,
                                          foreign key (dictionary_id) references public.t_dictionary (id)
                                              match simple on update no action on delete no action
);

create table public.t_user (
                               id bigint primary key not null default nextval('t_user_id_seq'::regclass),
                               creation_date timestamp without time zone,
                               email character varying(255),
                               last_login timestamp without time zone,
                               name character varying(255),
                               password character varying(255),
                               role character varying(255),
                               surname character varying(255),
                               username character varying(255),
                               affiliate_id bigint,
                               role_id bigint,
                               status boolean,
                               mobile character varying(255),
                               mobile_prefix character varying(255),
                               phone character varying(255),
                               phone_prefix character varying(255)
);
create unique index t_user_pk on t_user using btree (username);

create table public.t_visit (
                                id bigint primary key not null default nextval('t_visit_id_seq'::regclass),
                                campaign_id bigint,
                                cookie character varying(255),
                                creation_date timestamp without time zone,
                                data character varying(255),
                                read boolean not null,
                                reffweral_id bigint,
                                status boolean not null,
                                url character varying(255),
                                foreign key (campaign_id) references public.t_campaign (id)
                                    match simple on update no action on delete no action
);

create table public.t_wallet (
                                 id bigint primary key not null default nextval('t_wallet_id_seq'::regclass),
                                 description character varying(255),
                                 nome character varying(255),
                                 payed numeric(19,2),
                                 residual numeric(19,2),
                                 status boolean,
                                 total numeric(19,2),
                                 affiliate_id bigint,
                                 transaction_id bigint,
                                 foreign key (affiliate_id) references public.t_affiliate (id)
                                     match simple on update no action on delete no action
);

create table public.t_wallet_transaction (
                                             total_before double precision,
                                             total_after double precision,
                                             residual_before double precision,
                                             residual_after double precision,
                                             payed_before double precision,
                                             payed_after double precision,
                                             id bigint primary key not null default nextval('t_wallet_transaction_id_seq'::regclass),
                                             wallet_id bigint,
                                             date date,
                                             foreign key (wallet_id) references public.t_wallet (id)
                                                 match simple on update no action on delete no action
);

