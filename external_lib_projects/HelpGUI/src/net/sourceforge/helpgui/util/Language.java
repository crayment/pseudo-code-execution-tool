/*
 * 
 * Language.java - To get text from differents languages
 * Copyright (C) 2003 Alexandre THOMAS
 * alexthomas@free.fr
 * http://helpgui.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.helpgui.util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
  * Class that help to get text from differents file format 
	* @author Alexandre THOMAS
	*/
public class Language {

	/**Static variable*/
	private static Language language = null;
	
	/**Local variable */
	Locale currentLocale = null;
	/** For the resource file */
	ResourceBundle i18n = null;
	
	
	
	/** Get the instance of the language */
	public static Language getInstance() {
		if (language == null) {
			language = new Language();
		}
		return language;
	}
	
	
	/** Constructor of the language */
	private Language() {
		setLocale(Locale.getDefault());
	}
	
	/** Set the local value */
	public void setLocale(Locale locale) {
		currentLocale = locale;
		i18n = ResourceBundle.getBundle("net/sourceforge/helpgui/languages/i18n", currentLocale);
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	/* Return the text corresponding to the key */
	public String getText (String key) {
		return i18n.getString(key);
	}
}
