package er.r2d2w.components.relationships;

import java.text.SimpleDateFormat;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.directtoweb.D2WContext;
import com.webobjects.directtoweb.D2WModel;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSTimestamp;

import er.directtoweb.components.ERD2WStatelessComponent;
import er.extensions.foundation.ERXStringUtilities;
import er.extensions.foundation.ERXValueUtilities;
import er.extensions.localization.ERXLocalizer;
import er.r2d2w.assignments.R2DDefaultUserEntityAssignment;

@SuppressWarnings("unchecked")
public class R2D2WQueryAnyField extends ERD2WStatelessComponent {
	private static final NSArray<String> stringQualifierOperators = new NSArray<String>(new String[]{"starts with", "contains", "ends with", "is", "like"});
	private static final NSArray<String> relationalQualifierOperators = new NSArray<String>(new String[]{"=", "<>", "<", ">", "<=", ">="});
	private static final NSArray<String> standardQualifierOperators = stringQualifierOperators.arrayByAddingObjectsFromArray(relationalQualifierOperators);
	private static final NSArray<String> booleanQualifierOperators = new NSArray<String>(new String[] {"trueString", "falseString"});
	private static final NSArray<String> languageQualifierOperators = (NSArray<String>)ERXLocalizer.availableLanguages();
    
	private boolean ignoreQueryValue = false;
	private String item;
	private String labelID;
	private String queryValue;
	private String selectedKey;
	private D2WContext relationshipContext;
	private WODisplayGroup displayGroup;

	public R2D2WQueryAnyField(WOContext context) {
        super(context);
    }
	
	public void reset() {
		super.reset();
		ignoreQueryValue = false;
		item = null;
		labelID = null;
		queryValue = null;
		relationshipContext = null;
		displayGroup = null;
		selectedKey = null;
	}
	
	public D2WContext relationshipContext() {
		if(relationshipContext == null) {
			relationshipContext = new D2WContext(session());
			relationshipContext.setTask("query");
			relationshipContext.setEntity(relationship()==null?entity():relationship().destinationEntity());
		}
		return relationshipContext;
	}
	
	public WODisplayGroup displayGroup() {
		if(displayGroup == null) {
			displayGroup = (WODisplayGroup)valueForBinding("displayGroup");
		}
		return displayGroup;
	}
	
	public NSArray<EOAttribute> queryAttributes() {
		NSMutableArray<EOAttribute> result = new NSMutableArray<EOAttribute>();
		EOEntity entity = relationshipContext().entity();
		NSArray propertyKeys = ERXValueUtilities.arrayValueWithDefault(relationshipContext().valueForKey(D2WModel.DisplayPropertyKeysKey), NSArray.EmptyArray);
		for(Object key: propertyKeys) {
			String attributeName = (String)key;
			EOAttribute attribute = entity.attributeNamed(attributeName);
			if(attribute!=null) { result.add(attribute); }
		}
		return result.immutableClone();
	}

	public NSArray<String> displayKeys() {
		NSMutableArray<String> result = new NSMutableArray<String>();
		NSArray<EOAttribute> attributes = queryAttributes();
		D2WContext rc = relationshipContext();

		for(EOAttribute attribute: attributes) {
			rc.setPropertyKey(attribute.name());
			Class attributeClass = null;
			try {
				attributeClass = Class.forName(attribute.className());
			} catch(ClassNotFoundException e) {
				throw NSForwardException._runtimeExceptionForThrowable(e);
			}

			NSArray<String> operators = null;
			if(Number.class.isAssignableFrom(attributeClass)) {
				String v = attribute.valueType();
				if(v != null && v.length() == 1 && v.charAt(0) == EOAttribute._VTBoolean) {
					operators = booleanQualifierOperators;
				} else {
					operators = relationalQualifierOperators;
				}
			} else if (attribute.userInfo() != null && 
					("language".equals(attribute.userInfo().objectForKey("erPrototype")) || 
					attribute.equals(relationshipContext().valueForKey(R2DDefaultUserEntityAssignment.LANGUAGE_PROTO_KEY)))) {
				operators = languageQualifierOperators;
			} else if (Boolean.class.equals(attributeClass)) {
				operators = booleanQualifierOperators;
			} else if (String.class.equals(attributeClass)) {
				NSArray<String> opArray = (NSArray<String>)ERXValueUtilities.arrayValueWithDefault(rc.valueForKey("qualifierOperators"), new NSArray<String>());
				operators = (opArray.count() > 0)?opArray:standardQualifierOperators;
			} else {
				operators = relationalQualifierOperators;
			}
			
			StringBuilder sb = new StringBuilder(attribute.name()).append(NSKeyValueCodingAdditions.KeyPathSeparator);
			int sbLength = sb.length();
			for(String op: operators) {
				sb.append(op);
				result.add(sb.toString());
				sb.setLength(sbLength);
			}
		}
		return result.immutableClone();
	}
	
	public String displayString() {
		String itemString = item();
		int dotIndex = itemString.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator);
		String op = itemString.substring(dotIndex + 1, itemString.length());
		String prop = itemString.substring(0, dotIndex);
		
		D2WContext rc = relationshipContext();
		rc.setPropertyKey(prop);
		ERXLocalizer loc = ERXLocalizer.currentLocalizer();
		
		if(booleanQualifierOperators.contains(op)) {
			return (String)rc.valueForKey(op);
			
		} else if (languageQualifierOperators.contains(op)) {
			return loc.localizedStringForKeyWithDefault(op);
			
		} else if (NSTimestamp.class.getName().equals(rc.attribute().className())) {
			String opName = loc.localizedStringForKey(op);
			String formatHint = (String)rc.valueForKey("hintString");
			return opName + " " + formatHint;
			
		} else {
			return loc.localizedStringForKey(op);
		}
	}
	
	/**
	 * @return the labelID
	 */
	public String labelID() {
		return labelID;
	}

	/**
	 * @param labelID the labelID to set
	 */
	public void setLabelID(String labelID) {
		this.labelID = labelID;
	}

	public String group() {
		String item = item();
		int dotIndex = item.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator);
		return item.substring(0, dotIndex);
	}

	public String groupLabel() {
		relationshipContext().setPropertyKey(group());
		return relationshipContext().displayNameForProperty();
	}
	
	/**
	 * @return the item
	 */
	public String item() {
		return item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {
		this.item = item;
	}

	public String displayName() {
		if(relationship() == null) {
			return (String)relationshipContext().valueForKey("displayNameForEntity");			
		} else {
			return d2wContext().displayNameForProperty();
		}
	}

	/**
	 * @return the selectedKey
	 */
	public String selectedKey() {
		return selectedKey;
	}

	/**
	 * @param selectedKey the selectedKey to set
	 */
	public void setSelectedKey(String selectedKey) {
		this.selectedKey = selectedKey;
		
		WODisplayGroup dg = displayGroup();
		if(dg != null) {
			int dotIndex = selectedKey.indexOf(NSKeyValueCodingAdditions.KeyPathSeparator);
			String op = selectedKey.substring(dotIndex + 1, selectedKey.length());
			String prop = selectedKey.substring(0, dotIndex);
			D2WContext rc = relationshipContext();
			rc.setPropertyKey(prop);
			if(relationship()!=null) {
				prop = relationship().name() + NSKeyValueCodingAdditions.KeyPathSeparator + prop;
			}
			
			
			if(booleanQualifierOperators.contains(op)) {
				ignoreQueryValue = true;
				Boolean b = (op.equals("trueString"))?Boolean.TRUE:Boolean.FALSE;
				dg.queryMatch().takeValueForKey(b, prop);
				op = "=";
				
			} else if (languageQualifierOperators.contains(op)) {
				ignoreQueryValue = true;
				dg.queryMatch().takeValueForKey(op, prop);
				op = "=";
			}

			dg.queryOperator().takeValueForKey(op, prop);
		}
	}

	/**
	 * @return the queryValue
	 */
	public String queryValue() {
		return queryValue;
	}

	/**
	 * @param queryValue the queryValue to set
	 */
	public void setQueryValue(String queryValue) {
		this.queryValue = queryValue;
		WODisplayGroup dg = displayGroup();
		D2WContext rc = relationshipContext();
		if(!ignoreQueryValue && dg != null && rc.propertyKey() != null) {
			String prop = rc.propertyKey();
			if(relationship()!=null) {
				prop = relationship().name() + NSKeyValueCodingAdditions.KeyPathSeparator + prop;
			}

			if(!ERXStringUtilities.stringIsNullOrEmpty(queryValue)) {
				SimpleDateFormat sdf = null;
				if(NSTimestamp.class.getName().equals(rc.attribute().className())) {
					sdf = new SimpleDateFormat((String)rc.valueForKey("formatter"));
				}
				Object val = ERXStringUtilities.attributeValueFromString(rc.attribute(), queryValue, context().request().formValueEncoding(), sdf);
				if(val!=null) {
					dg.queryMatch().takeValueForKey(val, prop);
				} else {
					dg.queryMatch().remove(prop);
					dg.queryOperator().remove(prop);					
				}
			} else {
				dg.queryMatch().remove(prop);
				dg.queryOperator().remove(prop);
			}
		}
	}
	
}