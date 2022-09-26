package it.cleverad.engine.config;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import it.cleverad.engine.business.AffiliateChannelCommissionCampaignBusiness;
import it.cleverad.engine.business.TransactionBusiness;
import it.cleverad.engine.business.WalletBusiness;
import it.cleverad.engine.persistence.model.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.model.Transaction;
import it.cleverad.engine.persistence.model.Wallet;
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
                        mapping(type(TransactionBusiness.BaseCreateRequest.class), type(Transaction.class))
                                .fields("affiliateId", "affiliate.id")
                                .fields("campaignId", "campaign.id")
                                .fields("commissionId", "commission.id")
                                .fields("channelId", "channel.id")
                                .fields("walletId", "wallet.id");
                        mapping(type(AffiliateChannelCommissionCampaignBusiness.BaseCreateRequest.class), type(AffiliateChannelCommissionCampaign.class))
                                .fields("affiliateId", "affiliate.id")
                                .fields("campaignId", "campaign.id")
                                .fields("commissionId", "commission.id")
                                .fields("channelId", "channel.id");
                    }
                }).build();
    }

}