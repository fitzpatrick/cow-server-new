/**
 * Approved for Public Release: 10-4800. Distribution Unlimited.
 * Copyright 2011 The MITRE Corporation,
 * Licensed under the Apache License,
 * Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.wiredwidgets.cow.server.transform.v2;

import org.wiredwidgets.cow.server.api.model.v2.SubProcess;

/**
 *
 * @author JKRANES
 */
public class SubProcessActivityBuilder extends ActivityBuilderImpl<SubProcess> {

    public SubProcessActivityBuilder(ProcessContext context, SubProcess subProcess, ActivityBuilderFactory factory) {
        super(context, subProcess, factory);
    }

    @Override
    public void build() {
        NodeBuilder builder = this.createNodeBuilder(getContext(), getActivity(), NodeType.SUBPROCESS);
        builder.build(this);
        setLinkTarget(builder);
        setLinkSource(builder);
    }
}
