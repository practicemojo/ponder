package er.r2d2w.components.repetitions;

import com.webobjects.appserver.WOContext;

import er.directtoweb.components.repetitions.ERDAttributeRepetition;

public class R2DQueryFormRepetition extends ERDAttributeRepetition {
	/**
	 * Do I need to update serialVersionUID?
	 * See section 5.6 <cite>Type Changes Affecting Serialization</cite> on page 51 of the 
	 * <a href="http://java.sun.com/j2se/1.4/pdf/serial-spec.pdf">Java Object Serialization Spec</a>
	 */
	private static final long serialVersionUID = 1L;

	public R2DQueryFormRepetition(WOContext context) {
		super(context);
	}
}