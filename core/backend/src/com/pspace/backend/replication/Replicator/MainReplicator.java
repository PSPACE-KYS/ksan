/*
* Copyright (c) 2021 PSPACE, inc. KSAN Development Team ksan@pspace.co.kr
* KSAN is a suite of free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 
* 3 of the License.  See LICENSE for details
*
* 본 프로그램 및 관련 소스코드, 문서 등 모든 자료는 있는 그대로 제공이 됩니다.
* KSAN 프로젝트의 개발자 및 개발사는 이 프로그램을 사용한 결과에 따른 어떠한 책임도 지지 않습니다.
* KSAN 개발팀은 사전 공지, 허락, 동의 없이 KSAN 개발에 관련된 모든 결과물에 대한 LICENSE 방식을 변경 할 권리가 있습니다.
*/
package Replicator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pspace.backend.libs.Data.Constants;
import com.pspace.backend.libs.Ksan.Data.AgentConfig;
import com.pspace.ifs.ksan.libs.mq.MQReceiver;

import Filter.ReplicationFilter;
import config.ConfigManager;

public class MainReplicator {
	protected final Logger logger;
	protected final ConfigManager config;
	protected final AgentConfig agent;

	List<MQReceiver> filterReceivers = new ArrayList<MQReceiver>();
	List<MQReceiver> eventReceivers = new ArrayList<MQReceiver>();

	public MainReplicator() {
		this.logger = LoggerFactory.getLogger(MainReplicator.class);
		this.config = ConfigManager.getInstance();
		this.agent = AgentConfig.getInstance();
	}

	public boolean Start(int ThreadCount) {
		try {
			for (int index = 0; index < ThreadCount; index++) {
				// Filter Receiver 생성
				filterReceivers.add(new MQReceiver(
						agent.MQHost,
						agent.MQPort,
						agent.MQUser,
						agent.MQPassword,
						Constants.MQ_QUEUE_REPLICATION_S3_LOG,
						Constants.MQ_KSAN_LOG_EXCHANGE,
						false,
						"",
						Constants.MQ_BINDING_GW_LOG,
						new ReplicationFilter()));

				var eventCallback = new EventReplicator();
				eventCallback.SetRegion(config.getRegion());

				// Event Receiver 생성
				var eventReceiver = new MQReceiver(
						agent.MQHost,
						agent.MQPort,
						agent.MQUser,
						agent.MQPassword,
						Constants.MQ_QUEUE_REPLICATION_EVENT_ADD,
						Constants.MQ_KSAN_LOG_EXCHANGE,
						false,
						"",
						Constants.MQ_BINDING_REPLICATION_EVENT,
						eventCallback);
				eventReceivers.add(eventReceiver);
			}
			return true;
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
	}
}
