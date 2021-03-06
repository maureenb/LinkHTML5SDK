/*
 * Copyright 2009-2011 Prime Technology.
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
package org.helix.mobile.component.view;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.helix.mobile.component.tabbar.TabBar;
import org.primefaces.renderkit.CoreRenderer;

public class ViewRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        View view = (View) component;
        String swatch = view.getSwatch();
        String title = view.getTitle();

        writer.startElement("div", view);
        writer.writeAttribute("id", view.getId(), "id");
        writer.writeAttribute("data-role", "page", null);
        String cssExtraClass = "";
        if (view.isLayoutFullPage()) {
            writer.writeAttribute("style", "overflow: hidden;", null);
        } else {
            cssExtraClass= " hx-scrolling-page";
            writer.writeAttribute("style", "overflow-y: auto;", null);
        }
        writer.writeAttribute("class", "ui-page" + cssExtraClass, null);
        
        if (swatch != null) {
            writer.writeAttribute("data-theme", swatch, null);
        }

        if (title != null) {
            writer.writeAttribute("data-title", title, null);
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        View view = (View) component;
        
        /* Check to see if we are in a tab bar. If so, render a footer with the tab bar. */
        if (component.getParent() instanceof TabBar) {
            TabBar parentBar = (TabBar)component.getParent();
            parentBar.getAttributes().put("view", view);
            
            writer.startElement("div", component);
            writer.writeAttribute("data-role", "footer", null);
            writer.writeAttribute("data-position", "fixed", null);
            writer.writeAttribute("data-tap-toggle", "false", null);
            super.renderChild(context, parentBar);
            writer.endElement("div");
        }
        
        writer.endElement("div");

        startScript(writer, component.getClientId());
        
        writer.write("\n");
        writer.write("$(PrimeFaces.escapeClientId('" + component.getClientId() + "'))");
        writer.write(".on('orientationchange', function(event, ui) {\n");
        writer.write("Helix.Layout.layoutPage(); });");

        endScript(writer);
    }
}