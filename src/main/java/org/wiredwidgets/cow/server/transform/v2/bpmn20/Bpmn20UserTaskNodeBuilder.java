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

import org.omg.spec.bpmn._20100524.model.Assignment;
import org.omg.spec.bpmn._20100524.model.DataInput;
import org.omg.spec.bpmn._20100524.model.DataInputAssociation;
import org.omg.spec.bpmn._20100524.model.DataOutput;
import org.omg.spec.bpmn._20100524.model.DataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.InputSet;
import org.omg.spec.bpmn._20100524.model.IoSpecification;
import org.omg.spec.bpmn._20100524.model.OutputSet;
import org.omg.spec.bpmn._20100524.model.Property;
import org.omg.spec.bpmn._20100524.model.ResourceAssignmentExpression;
import org.omg.spec.bpmn._20100524.model.TDataAssociation;
import org.omg.spec.bpmn._20100524.model.TFormalExpression;
import org.omg.spec.bpmn._20100524.model.TPotentialOwner;
import org.omg.spec.bpmn._20100524.model.TUserTask;
import org.wiredwidgets.cow.server.api.model.v2.Task;
import org.wiredwidgets.cow.server.transform.v2.ProcessContext;

/**
 *
 * @author JKRANES
 */
public class Bpmn20UserTaskNodeBuilder extends Bpmn20FlowNodeBuilder<TUserTask, Task> {
	
	public static String TASK_INPUT_VARIABLES_NAME = "Variables";
	public static String TASK_OUTPUT_VARIABLES_NAME = "Variables";
    
    private IoSpecification ioSpec = new IoSpecification();
    private InputSet inputSet = new InputSet();
    private OutputSet outputSet = new OutputSet();
    // private static QName SOURCE_REF_QNAME = new QName("http://www.omg.org/spec/BPMN/20100524/MODEL","sourceRef");

    public Bpmn20UserTaskNodeBuilder(ProcessContext context, Task task) {
        super(context, new TUserTask(), task);
    }

    @Override
    protected void buildInternal() {

        Task source = getActivity();
        TUserTask t = getNode();
        t.setId(getContext().generateId("_")); // JBPM ID naming convention uses underscore prefix + sequence 
             
        source.setKey(t.getName());
        t.setName(source.getName());
           
        t.setIoSpecification(ioSpec);     
        ioSpec.getInputSets().add(inputSet);       
        ioSpec.getOutputSets().add(outputSet);
        
        // standard JBPM inputs
        Property varsProperty = getContext().getProcessVariable(Bpmn20ProcessBuilder.VARIABLES_PROPERTY);
        Property processNameProperty = getContext().getProcessVariable(Bpmn20ProcessBuilder.PROCESS_INSTANCE_NAME_PROPERTY);
        addDataInput(TASK_INPUT_VARIABLES_NAME, varsProperty);
        addDataOutput(TASK_OUTPUT_VARIABLES_NAME, varsProperty);
        addDataInput("ProcessInstanceName", processNameProperty);
        addDataInput("Comment", source.getDescription());
        addDataInput("Skippable", "false");
        addDataInput("TaskName", source.getName());
        
        if (source.getCandidateGroups() != null) {
        	addDataInput("GroupId", source.getCandidateGroups());
        }   
        
        // handle assignment
        addPotentialOwner(t, source.getAssignee());

    }

    @Override
    protected JAXBElement<TUserTask> createNode() {
        return factory.createUserTask(getNode());
    }
    
    private void addGroupAssginment(TUserTask t, String groupName) {
    	
    }
      
    private void addPotentialOwner(TUserTask t, String ownerName) {
        TFormalExpression formalExpr = new TFormalExpression();
        formalExpr.getContent().add(ownerName);

        ResourceAssignmentExpression resourceExpr = new ResourceAssignmentExpression();
        resourceExpr.setExpression(factory.createFormalExpression(formalExpr));

        TPotentialOwner owner = new TPotentialOwner();
        owner.setResourceAssignmentExpression(resourceExpr);
        t.getResourceRoles().add(factory.createPotentialOwner(owner));       
    }
  
    private String getInputRefName(String name) {
        return getNode().getId() + "_" + name + "Input"; // JBPM naming convention
    }
    
    protected void addDataInput(String name, String value) {     
        assignInputValue(addDataInput(name), value);
    }
    
    protected void addDataInput(String name,  Property value) {
    	assignInputValue(addDataInput(name), value);
    }
       
    protected DataInput addDataInput(String name) {
        DataInput dataInput = new DataInput();
        dataInput.setId(getInputRefName(name));
        dataInput.setName(name); 
        ioSpec.getDataInputs().add(dataInput);
        inputSet.getDataInputRefs().add(factory.createInputSetDataInputRefs(dataInput));  
        return dataInput;
    }
    
    /**
     * Create a new DataOutput linked to a new process level variable
     * @param name
     * @param addProcessVar true 
     * @return
     */
    protected DataOutput addDataOutput(String name, String processVarName) {	
    	return addDataOutput(name, getContext().addProcessVariable(processVarName, "String"));
    } 
    
    protected DataOutput addDataOutput(String name, Property prop) {
        DataOutput dataOutput = new DataOutput();
        String id = getNode().getId() + "_" + name + "Output"; // JBPM naming convention
        dataOutput.setId(id);
        dataOutput.setName(name); 
        ioSpec.getDataOutputs().add(dataOutput);
        outputSet.getDataOutputRefs().add(factory.createOutputSetDataOutputRefs(dataOutput)); 
        
        DataOutputAssociation doa = new DataOutputAssociation();
        getNode().getDataOutputAssociations().add(doa);
        
        // This part is not at all obvious. Determined correct approach by unmarshalling sample BPMN2 into XML
        // and then examining the java objects
        
        doa.getSourceReves().add(factory.createTDataAssociationSourceRef(dataOutput));
        doa.setTargetRef(prop);
  
        return dataOutput;    	
    }
    
    
    
    /**
     * Follows JBPM naming conventions
     * @param name
     * @param value 
     */
    protected void assignInputValue(DataInput dataInput, String value) {
        DataInputAssociation dia = new DataInputAssociation();
        getNode().getDataInputAssociations().add(dia);
        dia.setTargetRef(dataInput);

        Assignment assignment = new Assignment();
        
        TFormalExpression tfeFrom = new TFormalExpression();
        tfeFrom.getContent().add(value);
        assignment.setFrom(tfeFrom);
              
        TFormalExpression tfeTo = new TFormalExpression();
        tfeTo.getContent().add(dataInput.getId());
        assignment.setTo(tfeTo);
        
        dia.getAssignments().add(assignment); 
    }   
    
    
    protected void assignInputValue(DataInput dataInput, Property prop) {
        DataInputAssociation dia = new DataInputAssociation();
        getNode().getDataInputAssociations().add(dia);
        dia.setTargetRef(dataInput);     
        JAXBElement<Object> ref = factory.createTDataAssociationSourceRef(prop);
        dia.getSourceReves().add(ref);
    }     
    
}
