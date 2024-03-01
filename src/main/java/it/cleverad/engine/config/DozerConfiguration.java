package it.cleverad.engine.config;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import it.cleverad.engine.business.*;
import it.cleverad.engine.persistence.model.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DozerConfiguration {

    @Bean
    public Mapper beanMapper() {
        return DozerBeanMapperBuilder.create().withMappingBuilders(
                new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(type(WalletBusiness.BaseCreateRequest.class), type(Wallet.class))
                                .fields("affiliateId", "affiliate.id");
                        mapping(type(TransactionCPCBusiness.BaseCreateRequest.class), type(TransactionCPC.class))
                                .fields("affiliateId", "affiliate.id")
                                .fields("campaignId", "campaign.id")
                                .fields("commissionId", "commission.id")
                                .fields("channelId", "channel.id")
                                .fields("walletId", "wallet.id");
                        mapping(type(TransactionCPMBusiness.BaseCreateRequest.class), type(TransactionCPM.class))
                                .fields("affiliateId", "affiliate.id")
                                .fields("campaignId", "campaign.id")
                                .fields("commissionId", "commission.id")
                                .fields("channelId", "channel.id")
                                .fields("walletId", "wallet.id");
                        mapping(type(TransactionCPLBusiness.BaseCreateRequest.class), type(TransactionCPL.class))
                                .fields("campaignId", "campaign.id");
                        mapping(type(AffiliateChannelCommissionCampaignBusiness.BaseCreateRequest.class), type(AffiliateChannelCommissionCampaign.class))
                                .fields("affiliateId", "affiliate.id")
                                .fields("campaignId", "campaign.id")
                                .fields("commissionId", "commission.id")
                                .fields("channelId", "channel.id");
                    }
                }).build();
    }

}