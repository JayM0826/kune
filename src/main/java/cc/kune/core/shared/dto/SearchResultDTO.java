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
package cc.kune.core.shared.dto;

import java.util.List;

public class SearchResultDTO<T> {
  List<T> list;
  int size;

  public SearchResultDTO() {
    this(0, null);
  }

  public SearchResultDTO(final int size, final List<T> list) {
    this.list = list;
    this.size = size;
  }

  public List<T> getList() {
    return list;
  }

  public int getSize() {
    return size;
  }

  public void setList(final List<T> list) {
    this.list = list;
  }

  public void setSize(final int size) {
    this.size = size;
  }

  @Override
  public String toString() {
    return "SearchResultDTO[(" + size + "): " + list + "]";
  }

}
