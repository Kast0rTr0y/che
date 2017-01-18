/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.debug.shared.dto;

import org.eclipse.che.api.debug.shared.model.StackFrameDump;
import org.eclipse.che.dto.shared.DTO;

import java.util.List;

/** @author andrew00x */
@DTO
public interface StackFrameDumpDto extends StackFrameDump {
    @Override
    List<FieldDto> getFields();

    void setFields(List<FieldDto> fields);

    StackFrameDumpDto withFields(List<FieldDto> fields);

    @Override
    List<VariableDto> getVariables();

    void setVariables(List<VariableDto> variables);

    StackFrameDumpDto withVariables(List<VariableDto> variables);

    @Override
    LocationDto getLocation();

    void setLocation(LocationDto location);

    StackFrameDumpDto withLocation(LocationDto location);

}
