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
package cc.kune.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Indexed;

import cc.kune.domain.utils.HasId;

/**
 * GlobalizeCountries generated by hbm2java from original rails globalize schema
 * 
 * More info: http://en.wikipedia.org/wiki/Date_and_time_notation_by_country
 * http://en.wikipedia.org/wiki/Common_Locale_Data_Repository
 * 
 */
@Entity
@Indexed
@Table(name = "globalize_countries")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class I18nCountry implements HasId {

  @org.hibernate.annotations.Index(name="code")
  @Column(name = "code", length = 2)
  private String code;

  @Column(name = "currency_code", length = 3)
  private String currencyCode;

  @Column(name = "currency_decimal_sep", length = 2)
  private String currencyDecimalSep;

  @Column(name = "currency_format")
  private String currencyFormat;

  @Column(name = "date_format")
  private String dateFormat;

  @Column(name = "decimal_sep", length = 2)
  private String decimalSep;

  @org.hibernate.annotations.Index(name="english_name")
  @Column(name = "english_name")
  private String englishName;

  @Id
  // Is not a @GeneratedValue similar to I18nLanguage (we have already ids)
  @DocumentId
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "number_grouping_scheme")
  private String numberGroupingScheme;

  @Column(name = "thousands_sep", length = 2)
  private String thousandsSep;

  public I18nCountry() {
    this(null, null, null, null, null, null, null, null, null, null);
  }

  public I18nCountry(final Long id, final String code, final String currencyCode,
      final String currencyDecimalSep, final String currencyFormat, final String dateFormat,
      final String decimalSep, final String englishName, final String numberGroupingScheme,
      final String thousandsSep) {
    this.id = id;
    this.code = code;
    this.englishName = englishName;
    this.dateFormat = dateFormat;
    this.currencyFormat = currencyFormat;
    this.currencyCode = currencyCode;
    this.thousandsSep = thousandsSep;
    this.decimalSep = decimalSep;
    this.currencyDecimalSep = currencyDecimalSep;
    this.numberGroupingScheme = numberGroupingScheme;
  }

  public String getCode() {
    return this.code;
  }

  public String getCurrencyCode() {
    return this.currencyCode;
  }

  public String getCurrencyDecimalSep() {
    return this.currencyDecimalSep;
  }

  public String getCurrencyFormat() {
    return this.currencyFormat;
  }

  public String getDateFormat() {
    return this.dateFormat;
  }

  public String getDecimalSep() {
    return this.decimalSep;
  }

  public String getEnglishName() {
    return this.englishName;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  public String getNumberGroupingScheme() {
    return this.numberGroupingScheme;
  }

  public String getThousandsSep() {
    return this.thousandsSep;
  }

  public void setCode(final String code) {
    this.code = code;
  }

  public void setCurrencyCode(final String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public void setCurrencyDecimalSep(final String currencyDecimalSep) {
    this.currencyDecimalSep = currencyDecimalSep;
  }

  public void setCurrencyFormat(final String currencyFormat) {
    this.currencyFormat = currencyFormat;
  }

  public void setDateFormat(final String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public void setDecimalSep(final String decimalSep) {
    this.decimalSep = decimalSep;
  }

  public void setEnglishName(final String englishName) {
    this.englishName = englishName;
  }

  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  public void setNumberGroupingScheme(final String numberGroupingScheme) {
    this.numberGroupingScheme = numberGroupingScheme;
  }

  public void setThousandsSep(final String thousandsSep) {
    this.thousandsSep = thousandsSep;
  }

}
