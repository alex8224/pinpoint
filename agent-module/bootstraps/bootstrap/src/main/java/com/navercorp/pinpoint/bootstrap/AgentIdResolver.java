/*
 * Copyright 2020 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.bootstrap;

import com.navercorp.pinpoint.bootstrap.util.StringUtils;
import com.navercorp.pinpoint.common.util.AgentUuidUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Woonduk Kang(emeroad)
 */
public class AgentIdResolver {
    private final BootLogger logger = BootLogger.getLogger(this.getClass());

    private final List<AgentProperties> agentPropertyList;

    private final IdValidator idValidator = new IdValidator();

    public AgentIdResolver(List<AgentProperties> agentPropertyList) {
        this.agentPropertyList = Objects.requireNonNull(agentPropertyList, "agentPropertyList");
    }

    public AgentIds resolve() {
        String agentId = getAgentId();
        if (StringUtils.isEmpty(agentId)) {
            logger.info("Failed to resolve AgentId(-Dpinpoint.agentId)");
            agentId = newRandomAgentId();
            logger.info("Auto generate AgentId='" + agentId + "'");
        }

        final String applicationName = getApplicationName();
        if (StringUtils.isEmpty(applicationName)) {
            logger.warn("Failed to resolve ApplicationName(-Dpinpoint.applicationName)");
            return null;
        }

        final String agentName = getAgentName();
        if (StringUtils.isEmpty(agentName)) {
            logger.info("No AgentName(-Dpinpoint.agentName) provided, it's optional!");
        }

        return new AgentIds(agentId, agentName, applicationName);
    }

    private String newRandomAgentId() {
        UUID agentUUID = UUID.randomUUID();
        return AgentUuidUtils.encode(agentUUID);
    }

    private String getAgentId() {
        for (AgentProperties agentProperty : agentPropertyList) {
            final String agentId = agentProperty.getAgentId();
            if (StringUtils.isEmpty(agentId)) {
                continue;
            }
            if (idValidator.validateAgentId(agentProperty.getType(), agentId)) {
                logger.info(agentProperty.getType().getDesc() + " " + agentProperty.getAgentIdKey() + "=" + agentId);
                return agentId;
            }
        }
        return "";
    }

    private String getAgentName() {
        for (AgentProperties agentProperty : agentPropertyList) {
            final String agentName = agentProperty.getAgentName();
            if (idValidator.validateAgentName(agentProperty.getType(), agentName)) {
                logger.info(agentProperty.getType().getDesc() + " " + agentProperty.getAgentNameKey() + "=" + agentName);
                return agentName;
            }
        }
        return "";
    }

    private String getApplicationName() {
        for (AgentProperties agentProperty : agentPropertyList) {
            final String applicationName = agentProperty.getApplicationName();
            if (StringUtils.isEmpty(applicationName)) {
                continue;
            }
            if (idValidator.validateApplicationName(agentProperty.getType(), applicationName)) {
                logger.info(agentProperty.getType().getDesc() + " " + agentProperty.getApplicationNameKey() + "=" + applicationName);
                return applicationName;
            }
        }
        return "";
    }

}
