/**
 * Approved for Public Release: 10-4800. Distribution Unlimited. Copyright 2011
 * The MITRE Corporation, Licensed under the Apache License, Version 2.0 (the
 * "License");
 *
 * You may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wiredwidgets.cow.server.transform.v2.bpmn20;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.omg.spec.bpmn._20100524.model.DataOutput;
import org.omg.spec.bpmn._20100524.model.DataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.OutputSet;
import org.omg.spec.bpmn._20100524.model.Property;
import org.omg.spec.bpmn._20100524.model.TIntermediateCatchEvent;
import org.omg.spec.bpmn._20100524.model.TSignalEventDefinition;
import org.wiredwidgets.cow.server.api.model.v2.Activity;
import org.wiredwidgets.cow.server.transform.v2.ProcessContext;

/**
 *
 * @author JKRANES
 */
public class Bpmn20SignalEventNodeBuilder extends Bpmn20FlowNodeBuilder<TIntermediateCatchEvent, Activity> {
	

    public Bpmn20SignalEventNodeBuilder(ProcessContext context, Activity activity) {
        super(context, new TIntermediateCatchEvent(), activity);
    }

    @Override
    protected void buildInternal() {

        getNode().setId(getContext().generateId("_")); // JBPM ID naming convention uses underscore prefix + sequence                
        getNode().setName("Signal");
        // source.setKey(t.getId());
                  
        Property exitProperty = getContext().getProcessVariable(Bpmn20ProcessBuilder.PROCESS_EXIT_PROPERTY);
        addDataOutput("event", exitProperty);
        
        TSignalEventDefinition def = factory.createTSignalEventDefinition();
        def.setSignalRef(new QName("exit"));
        getNode().getEventDefinitions().add(factory.createSignalEventDefinition(def));

    }

    @Override
    protected JAXBElement<TIntermediateCatchEvent> createNode() {
        return factory.createIntermediateCatchEvent(getNode());
    }
    

    protected DataOutput addDataOutput(String name, String processVarName) {	
    	return addDataOutput(name, getContext().addProcessVariable(processVarName, "String"));
    } 
    
    protected DataOutput addDataOutput(String name, Property prop) {
        DataOutput dataOutput = new DataOutput();
        String id = getNode().getId() + "_" + name + "Output"; // JBPM naming convention
        dataOutput.setId(id);
        dataOutput.setName(name); 
        getNode().getDataOutputs().add(dataOutput);
        
        OutputSet outputs = new OutputSet();
        outputs.getDataOutputRefs().add(factory.createOutputSetDataOutputRefs(dataOutput));
        getNode().setOutputSet(outputs);
        
        DataOutputAssociation doa = new DataOutputAssociation();
        getNode().getDataOutputAssociations().add(doa);
        
        // This part is not at all obvious. Determined correct approach by unmarshalling sample BPMN2 into XML
        // and then examining the java objects
        
        doa.getSourceReves().add(factory.createTDataAssociationSourceRef(dataOutput));
        doa.setTargetRef(prop);
  
        return dataOutput;    	
    }
    
    
}
