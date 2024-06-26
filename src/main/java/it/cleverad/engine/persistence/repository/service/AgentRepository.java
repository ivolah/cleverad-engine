package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Agent;
import it.cleverad.engine.persistence.model.service.WidgetAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {

    @Query(nativeQuery = true, value = "SELECT count(*) as count, device_name as deviceName from t_agent " +
            " where  (:campaign_id is null or campaign_id = :campaign_id) " +
            " and (:affiliate_id is null or affiliate_id = :affiliate_id) " +
            " group by device_name"+
            " order by count DESC")
    List<WidgetAgent> getDevice(@Param("campaign_id") String campaignId, @Param("affiliate_id") String affiliateId);

    @Query(nativeQuery = true, value = "SELECT count(*) as count, os_name as osName, os_version as osVersion from t_agent " +
            " where  (:campaign_id is null or campaign_id = :campaign_id) " +
            " and (:affiliate_id is null or affiliate_id = :affiliate_id) " +
            " group by os_name, os_version "+
            " order by count DESC")
    List<WidgetAgent> geOs(@Param("campaign_id") String campaignId, @Param("affiliate_id") String affiliateId);

    @Query(nativeQuery = true, value = "SELECT count(*) as count, agent_name as agentName, agent_version as agentVersion from t_agent " +
            " where  (:campaign_id is null or campaign_id = :campaign_id) " +
            " and (:affiliate_id is null or affiliate_id = :affiliate_id) " +
            " group by agent_name,agent_version"+
            " order by count DESC")
    List<WidgetAgent> searchAgentDetailed(@Param("campaign_id") String campaignId, @Param("affiliate_id") String affiliateId);

    @Query(nativeQuery = true, value = "SELECT count(*) as count, agent_name as agentName from t_agent " +
            " where  (:campaign_id is null or campaign_id = :campaign_id) " +
            " and (:affiliate_id is null or affiliate_id = :affiliate_id) " +
            " group by agent_name" +
            " order by count DESC")
    List<WidgetAgent> getAgent(@Param("campaign_id") String campaignId, @Param("affiliate_id") String affiliateId);


}