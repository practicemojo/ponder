To use this framework, I would recommend you work with 
Project Wonder source in your workspace.  You can find 
instructions on how to do this on the wiki.

The Project Templates.zip file is a compressed archive of 
project templates for use with this framework. See the 
readme file contained in that archive for more information.

The Templates folder contains eoGen templates that I use
for generating model classes.  They're pretty nice now,
but I'm still running into cases where they need tweaking.

An example of how to add Wonder's ajax to an existing 
framework's edit pages is included in the AjaxLook.zip 
archive. The example should launch and allow you to go
to an EditAddress configuration page to test out the
dependency observers that work between different 
property level components.

Since this is a bit of a grab bag of goodies already,
I'm throwing in a zip archive of FreshnessExplorer
so I can clear my workspace a bit :)

TODO

Some things are currently works in progress. 
I am also wanting to
do a better job of the direct actions and work in some
sort of automated SEO stuff.
I'm considering adding some ajax-y things, but I think
I would rather start that in a new framework.  I need
to add nice calendars for day, week, and year view. I'd
like to add iCal support with that. I need to figure
out why the EditToOneRelationship component on a
EditRelationship page does not show new EO which is on
the other side of the relationship. I'd like to use
ERXQualifierTraversal in the methods that build up
qualifiers for list DA urls. I'd like to update the
derived counts to be less hackish and possibly work
across many-to-many joins. I'd like to revise
the current html to be a bit better structured.
I need to look at ERAttachments url encoding
of file names in the HTML, since it doesn't seem to escape
ampersands and that causes pink error boxes in the XHTML.
Add headings to the error and instruction panels. Just
revise those anyway, and possibly dump put the errors
beside the appropriate form fields instead of just in
panels. Add a new list repetition that does
ERDInspects instead of a table.

Yeah, just a few things to fix. :)