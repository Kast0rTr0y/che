/*
 * Copyright (c) 2016-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */

import {org} from "../../../api/dto/che-dto"
import {AuthData} from "../auth/auth-data";
import {Websocket} from "../../../spi/websocket/websocket";
import {HttpJsonRequest} from "../../../spi/http/default-http-json-request";
import {DefaultHttpJsonRequest} from "../../../spi/http/default-http-json-request";
import {HttpJsonResponse} from "../../../spi/http/default-http-json-request";
import {MessageBus} from "../../../spi/websocket/messagebus";
import {SystemStopEventPromiseMessageBusSubscriber} from "./system-stop-event-promise-subscriber";
import {Log} from "../../../spi/log/log";

/**
 * System class allowing to get state of system and perform graceful stop, etc.
 * @author Florent Benoit
 */
export class System {

    /**
     * Authentication data
     */
    authData:AuthData;

    /**
     * websocket.
     */
    websocket:Websocket;

    constructor(authData:AuthData) {
        this.authData = authData;
        this.websocket = new Websocket();
    }

    /**
     * Get state of the system
     */
    getState():Promise<org.eclipse.che.api.system.shared.dto.SystemStateDto> {
        var jsonRequest:HttpJsonRequest = new DefaultHttpJsonRequest(this.authData, '/api/system/state', 200);
        return jsonRequest.request().then((jsonResponse:HttpJsonResponse) => {
            return jsonResponse.asDto(org.eclipse.che.api.system.shared.dto.SystemStateDtoImpl);
        });
    }

    /**
     * Get the message bus for the given System state DTO.
     * @param systemStateDto the current DTO
     * @returns {Promise<MessageBus>}
     */
    getMessageBus(systemStateDto : org.eclipse.che.api.system.shared.dto.SystemStateDto): Promise<MessageBus> {

        // get link for websocket
        var websocketLink:string;
        systemStateDto.getLinks().forEach(stateLink => {
            if ('system.state.channel' === stateLink.getRel()) {
                websocketLink = stateLink.getHref();
            }
        });

        return this.websocket.getMessageBus(websocketLink + '?token=' + this.authData.getToken());
    }


    /**
     * Stop the server and return a promise that will wait for the ready to shutdown event
     */
    gracefulStop():Promise<org.eclipse.che.api.system.shared.dto.SystemStateDto> {

        let currentSystemStateDto : org.eclipse.che.api.system.shared.dto.SystemStateDto;
        let callbackSubscriber : SystemStopEventPromiseMessageBusSubscriber;

        // get workspace DTO
        return this.getState().then((systemStateDto: org.eclipse.che.api.system.shared.dto.SystemStateDto) => {
            currentSystemStateDto = systemStateDto;
            return this.getMessageBus(systemStateDto);
        }).then((messageBus: MessageBus) => {
            callbackSubscriber = new SystemStopEventPromiseMessageBusSubscriber(messageBus);
            let channelToListen : string;
            currentSystemStateDto.getLinks().forEach(stateLink => {
                if ('system.state.channel' === stateLink.getRel()) {
                    channelToListen = stateLink.getParameters()[0].getDefaultValue();
                }
            });
            return messageBus.subscribeAsync(channelToListen, callbackSubscriber);
        }).then((subscribed: string) => {
            var jsonRequest:HttpJsonRequest = new DefaultHttpJsonRequest(this.authData, '/api/system/stop', 204).setMethod('POST');
            return jsonRequest.request().then((jsonResponse:HttpJsonResponse) => {
                return;
            }).then(() => {
                return callbackSubscriber.promise;
            }).then(() => {
                return this.getState();
            });
        });


    }
}
