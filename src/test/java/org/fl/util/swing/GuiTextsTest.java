/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.util.swing;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GuiTextsTest {

	@BeforeEach
	void clearCache() {
		ResourceBundle.clearCache();
	}
	
	@Test
	void testFranceText() {
		
		GuiTexts.init(Locale.FRANCE);
		
		assertThat(GuiTexts.getText("appTabbedPane.information.tabTitle")).isEqualTo("Informations");
		assertThat(GuiTexts.getText("appTabbedPane.information.IPlookUp")).isEqualTo("Recherche du nom correspondant aux adresses IP (peut être lent)");
		
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Configuration des logs");
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.selectLogger")).isEqualTo("Sélectionner le Logger depuis la racine:");
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.configureLogger")).isEqualTo("Configurer le Logger");
		
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.tabTitle")).isEqualTo("Affichage du log");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.ignoreAccentsCheckBox")).isEqualTo("Ignorer les accents");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.ignoreFormattingCheckBox")).isEqualTo("Ignorer le formatage");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.showCategoriesButton")).isEqualTo("Montrer les categories");
	}
	
	@Test
	void testFrenchText() {

		GuiTexts.init(Locale.FRENCH);
		
		assertThat(GuiTexts.getText("appTabbedPane.information.tabTitle")).isEqualTo("Informations");
		assertThat(GuiTexts.getText("appTabbedPane.information.IPlookUp")).isEqualTo("Recherche du nom correspondant aux adresses IP (peut être lent)");
		
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Configuration des logs");
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.selectLogger")).isEqualTo("Sélectionner le Logger depuis la racine:");
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.configureLogger")).isEqualTo("Configurer le Logger");
		
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.tabTitle")).isEqualTo("Affichage du log");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.ignoreAccentsCheckBox")).isEqualTo("Ignorer les accents");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.ignoreFormattingCheckBox")).isEqualTo("Ignorer le formatage");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.showCategoriesButton")).isEqualTo("Montrer les categories");
	}
	
	@Test
	void testFrenchBelgiumText() {

		GuiTexts.init("fr", "BE");
		
		assertThat(GuiTexts.getText("appTabbedPane.information.tabTitle")).isEqualTo("Informations");
		assertThat(GuiTexts.getText("appTabbedPane.information.IPlookUp")).isEqualTo("Recherche du nom correspondant aux adresses IP (peut être lent)");
		
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Configuration des logs");
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.selectLogger")).isEqualTo("Sélectionner le Logger depuis la racine:");
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.configureLogger")).isEqualTo("Configurer le Logger");
		
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.tabTitle")).isEqualTo("Affichage du log");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.ignoreAccentsCheckBox")).isEqualTo("Ignorer les accents");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.ignoreFormattingCheckBox")).isEqualTo("Ignorer le formatage");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.showCategoriesButton")).isEqualTo("Montrer les categories");
	}
	
	@Test
	void testUSText() {
		
		GuiTexts.init(Locale.US);
		
		assertThat(GuiTexts.getText("appTabbedPane.information.tabTitle")).isEqualTo("Informations");
		assertThat(GuiTexts.getText("appTabbedPane.information.IPlookUp")).isEqualTo("Do name lookup on IP addresses (may be slow)");
		
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.tabTitle")).isEqualTo("Log configuration");
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.selectLogger")).isEqualTo("Select the logger from the following root:");
		assertThat(GuiTexts.getText("appTabbedPane.logConfiguration.configureLogger")).isEqualTo("Configure logger");
		
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.tabTitle")).isEqualTo("Log display");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.ignoreAccentsCheckBox")).isEqualTo("Ignore accents");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.ignoreFormattingCheckBox")).isEqualTo("Ignore formatting");
		assertThat(GuiTexts.getText("appTabbedPane.logDisplay.showCategoriesButton")).isEqualTo("Show categories");
	}
	
	@Test
	void testDefaultLocale() {
		// Default is Local.US, not the default Locale
		assertThat(GuiTexts.getDefaultLocale()).isEqualTo(Locale.ENGLISH);
	}
	
	@Test
	void testDefaultText() {
		
		GuiTexts.reset();
		String textKey = "appTabbedPane.logConfiguration.tabTitle";
		String defaultText = GuiTexts.getText(textKey);
		
		GuiTexts.init(Locale.ENGLISH);		
		assertThat(GuiTexts.getText(textKey)).isEqualTo(defaultText);
	}
	
	@Test
	void testUnsupportedLocaleText() {
		
		GuiTexts.init(Locale.GERMANY);
		String textKey = "appTabbedPane.logConfiguration.tabTitle";
		String defaultText = GuiTexts.getText(textKey);
		
		GuiTexts.init(GuiTexts.getDefaultLocale());		
		assertThat(GuiTexts.getText(textKey)).isEqualTo(defaultText);
	}
}
