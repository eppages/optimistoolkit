package eu.optimis.elasticityengine.manifest;

import java.util.List;

/**
 * Object structure to be parsed from manifest
 * 
 * @author Daniel Espling (<a href="mailto:espling@cs.umu.se">espling@cs.umu.se</a>)
 *Copyright (C) 2012 Umeå University

* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
public class RulesList {
	@Override
	public String toString() {
        return "Items [rules=" + rules + "]";
	}

    private List<Rule> rules;

	public List<Rule> getRules() {
        return rules;
	}

    public void setRules(List<Rule> rules) {
        this.rules = rules;
	}
}
