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

package org.wiredwidgets.cow.server.completion;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BypassableActivityEvaluator extends AbstractEvaluator<BypassableActivity> {

    @Override
    public void evaluateInternal() {
        evaluate(activity.getActivities());
        // reverse wrapping flag
        activity.getWrappedActivity().setWrapped(false);
        setCompletionState(activity.getActivities().getCompletionState());
        // set the completion state of the wrapped activity to the completion state of the wrapper
        activity.getWrappedActivity().setCompletionState(activity.getActivities().getCompletionState());
    }
}
