/*
 * Copyright 2005-2026 the original author or authors.
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
package org.openwms.core.process.execution.timing;

import jakarta.servlet.http.HttpServletRequest;
import org.ameba.http.MeasuredRestController;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.process.execution.timing.api.TimerConfigurationVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * A TimerConfigurationController.
 *
 * @author Heiko Scherrer
 */
@MeasuredRestController
class TimerConfigurationController extends AbstractWebController {

    private final TimerConfigurationMapper mapper;
    private final TimerConfigurationService service;

    TimerConfigurationController(TimerConfigurationMapper mapper, TimerConfigurationService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping("/timers/{pKey}")
    public ResponseEntity<TimerConfigurationVO> findByPKey(@PathVariable String pKey) {
        return ResponseEntity.ok(mapper.convertToVO(service.findByPKey(pKey)));
    }

    @PostMapping("/timers")
    public ResponseEntity<Void> create(@RequestBody TimerConfigurationVO vo, HttpServletRequest req) {
        var newInstance = service.create(mapper.convert(vo));
        return ResponseEntity.created(getLocationURIForCreatedResource(req, newInstance.getPersistentKey())).build();
    }

    @DeleteMapping("/timers/{pKey}")
    public ResponseEntity<Void> delete(@PathVariable String pKey) {
        service.delete(pKey);
        return ResponseEntity.noContent().build();
    }
}
