/*
 *
 * Copyright (C) 2007-2013 Licensed to the Comunes Association (CA) under
 * one or more contributor license agreements (see COPYRIGHT for details).
 * The CA licenses this file to you under the GNU Affero General Public
 * License version 3, (the "License"); you may not use this file except in
 * compliance with the License. This file is part of kune.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cc.kune.core.shared.domain;

import com.google.gwt.user.client.rpc.IsSerializable;

/*
 * The Enum GroupListMode describe the group list behavior
 * 
 * |---------------+-----------------------------------------------------------|
 * | GroupListMode |                                                           |
 * |---------------+-----------------------------------------------------------|
 * | NAME          | Description                                               |
 * |---------------+-----------------------------------------------------------|
 * | EVERYONE      | means that everybody is in the list                       |
 * | NOBODY        | means that nobody is in the list (equals to a empty list) |
 * | NORMAL        | a normal list of groups                                   |
 * |---------------+-----------------------------------------------------------|
 * 
 */
public enum GroupListMode implements IsSerializable {

  /** EVERYONE: means that everybody is in the list */
  EVERYONE,
  /** NOBODY: means that nobody is in the list (equals to a empty list) */
  NOBODY,
  /** NORMAL a normal list of groups */
  NORMAL;
}
