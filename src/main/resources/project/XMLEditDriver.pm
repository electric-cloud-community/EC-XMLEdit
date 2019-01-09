# -------------------------------------------------------------------------
# Package
#    XMLEditDriver
#
# Dependencies
#    None
#
# Purpose
#    A perl library that encapsulates the logic to execute Text Edition actions on files
#
# Plugin Version
#    1.0
#
# Date
#    07/13/2012
#
# Engineer
#    Andres Arias
#
# Copyright (c) 2012 Electric Cloud, Inc.
# All rights reserved
# -------------------------------------------------------------------------

package XMLEditDriver;

# -------------------------------------------------------------------------
# Includes
# -------------------------------------------------------------------------
use utf8;
use Carp;
use strict;
use Encode;
use warnings;
use ElectricCommander;
use open IO => ':encoding(utf8)';
use XML::LibXML;

# -------------------------------------------------------------------------
# Constants
# -------------------------------------------------------------------------
use constant {
    DEFAULT_DEBUG   => 1,
    EMPTY           => q{''},
    ERROR           => 1,
    SUCCESS         => 0,
};

# -------------------------------------------------------------------------
# Main functions
# -------------------------------------------------------------------------

###########################################################################

=head2 new
 
  Title    : new
  Usage    : new($ec, $opts);
  Function : Object constructor for XMLEditDriver.
  Returns  : XMLEditDriver instance
  Args     : named arguments:
           : -_cmdr => ElectricCommander instance
           : -_opts => hash of parameters from procedures
           :
=cut

###########################################################################
sub new {
    my $class = shift;
    my $self = {
                 _cmdr => shift,
                 _opts => shift,
               };
    $self->{_xml_parser} = XML::LibXML->new();

    bless $self, $class;
    return $self;
}

###########################################################################

=head2 xpath_query
 
  Title    : xpath_query
  Usage    : $self->xpath_query();
  Function : Execute an xpath query on a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub xpath_query {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{xpath_query});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $source = $self->opts->{source};
    my $query  = $self->opts->{xpath_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Execute xPath
    my @nodes    = $doc->findnodes($query);
    my $quantity = scalar(@nodes);

    if ($quantity == 0) {
        @nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{xpath_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    #Print results
    foreach my $element (@nodes) {
        print $element->to_literal . "\n";
    }

    return;
}

###########################################################################

=head2 read_element
 
  Title    : read_element
  Usage    : $self->read_element();
  Function : Read the value of an xml element from a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub read_element {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $prop   = $self->opts->{element_outpp};
    my $type   = "to_literal";
    my $source = $self->opts->{source};
    my $query  = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Execute xPath
    my @nodes    = $doc->findnodes($query);
    my $quantity = scalar(@nodes);

    if ($quantity == 0) {
        @nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @nodes = ($nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @nodes = ($nodes[$quantity - 1]); }

    # Set the return type
    if ($self->opts->{return_type} eq 'tag') { $type = "toString"; }
    if ($self->opts->{selection}   eq 'all') { $prop = $self->opts->{element_outpsp}; }

    #Print and/or save results
    my $index = 1;
    foreach my $element (@nodes) {
        print $element->$type . "\n";
        if ($prop ne EMPTY) {
            if ($self->opts->{selection} eq 'all') {
                my $tag_name = $element->toString;
                $tag_name =~ /<([A-Za-z0-9]+).*/;
                $self->myProp->setProp($self->opts->{element_outpsp} . "/$1_$index", $element->$type);
            }
            else {
                $self->myProp->setProp($self->opts->{element_outpp}, $element->$type);
            }
        }

        $index++;
    }
    return;
}

###########################################################################

=head2 read_attribute
 
  Title    : read_attribute
  Usage    : $self->read_attribute();
  Function : Read the value of an xml attribute from a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub read_attribute {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $prop      = $self->opts->{attribute_outpp};
    my $type      = "to_literal";
    my $source    = $self->opts->{source};
    my $attribute = $self->opts->{attribute};

    if ($self->opts->{read_query} !~ m/.+\@$attribute$/imgs) {
        $self->opts->{read_query} .= "/\@$attribute";
    }

    my $query = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Execute xPath
    my @nodes    = $doc->findnodes($query);
    my $quantity = scalar(@nodes);

    if ($quantity == 0) {
        @nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @nodes = ($nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @nodes = ($nodes[$quantity - 1]); }

    # Set the return type
    if ($self->opts->{return_type} eq 'structure') { $type = "toString"; }
    if ($self->opts->{selection}   eq 'all')       { $prop = $self->opts->{attribute_outpsp}; }

    #Print and/or save results
    my $index = 1;
    foreach my $element (@nodes) {
        print $element->$type . "\n";
        if ($prop ne EMPTY) {
            if ($self->opts->{selection} eq 'all') {
                $self->myProp->setProp($self->opts->{attribute_outpsp} . "/$attribute\_$index", $element->$type);
            }
            else {
                $self->myProp->setProp($self->opts->{attribute_outpp}, $element->$type);
            }
        }
        $index++;
    }
    return;
}

###########################################################################

=head2 insert_element
 
  Title    : insert_element
  Usage    : $self->insert_element();
  Function : Insert an xml element on a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub insert_element {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $source = $self->opts->{source};
    my $query  = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Get the root
    my $root = $doc->getDocumentElement;

    #Execute xPath
    my @parent_nodes = $doc->findnodes($query);
    my $quantity     = scalar(@parent_nodes);

    if ($quantity == 0) {
        @parent_nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @parent_nodes = ($parent_nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @parent_nodes = ($parent_nodes[$quantity - 1]); }

    #Insert element(s)
    my $parser = $self->{_xml_parser};
    foreach my $parent (@parent_nodes) {

        #Create the new element
        my $new_element = $doc->createElement($self->opts->{element_tag});
        my $content     = $parser->parse_balanced_chunk($self->opts->{element});
        $new_element->appendChild($content);

        if ($parent->toString(1) ne $root->toString(1)) {
            $parent->addChild($new_element);
        }
        else {
            $root->addChild($new_element);
        }
    }
    print $doc->toString(1);

    #Save results
    $self->save_xml($source, $doc);

    return;
}

###########################################################################

=head2 insert_attribute
 
  Title    : insert_attribute
  Usage    : $self->insert_attribute();
  Function : Insert an xml attribute in an element on a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub insert_attribute {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $source = $self->opts->{source};
    my $query  = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Get the root
    my $root = $doc->getDocumentElement;

    #Execute xPath
    my @elements_nodes = $doc->findnodes($query);
    my $quantity       = scalar(@elements_nodes);

    if ($quantity == 0) {
        @elements_nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @elements_nodes = ($elements_nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @elements_nodes = ($elements_nodes[$quantity - 1]); }

    #Insert attribute(s)
    foreach my $parent (@elements_nodes) {

        #Add the new attribute
        $parent->setAttribute($self->opts->{attribute_name}, $self->opts->{attribute_value});
    }
    print $doc->toString(1);

    #Save results
    $self->save_xml($source, $doc);

    return;
}

###########################################################################

=head2 delete_element
 
  Title    : delete_element
  Usage    : $self->delete_element();
  Function : Delete an xml element from a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub delete_element {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $source = $self->opts->{source};
    my $query  = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Execute xPath
    my @nodes    = $doc->findnodes($query);
    my $quantity = scalar(@nodes);

    if ($quantity == 0) {
        @nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @nodes = ($nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @nodes = ($nodes[$quantity - 1]); }

    #Delete element(s)
    foreach my $element (@nodes) {

        #Delete the element
        my $parent_node = $element->parentNode;
        $parent_node->removeChild($element);
    }
    print $doc->toString(1);

    #Save results
    $self->save_xml($source, $doc);

    return;
}

###########################################################################

=head2 delete_attribute
 
  Title    : delete_attribute
  Usage    : $self->delete_attribute();
  Function : Delete an xml attribute from an element in a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub delete_attribute {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Attribute: ' . $self->opts->{attribute_name});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $source = $self->opts->{source};
    my $query  = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Get the root
    my $root = $doc->getDocumentElement;

    #Execute xPath
    my @elements_nodes = $doc->findnodes($query);
    my $quantity       = scalar(@elements_nodes);

    if ($quantity == 0) {
        @elements_nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @elements_nodes = ($elements_nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @elements_nodes = ($elements_nodes[$quantity - 1]); }

    #Delete attribute(s)
    foreach my $element (@elements_nodes) {
        if ($element->hasAttribute($self->opts->{attribute_name})) {

            #Delete the new attribute
            $element->removeAttribute($self->opts->{attribute_name});
        }
        else {
            $self->debug_msg(0, 'Attribute \'' . $self->opts->{attribute_name} . '\' not found.');
        }
    }
    print $doc->toString(1);

    #Save results
    $self->save_xml($source, $doc);

    return;
}

###########################################################################

=head2 update_element
 
  Title    : update_element
  Usage    : $self->update_element();
  Function : Update an xml element from a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub update_element {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $source = $self->opts->{source};
    my $query  = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Get the root
    my $root = $doc->getDocumentElement;

    #Execute xPath
    my @nodes    = $doc->findnodes($query);
    my $quantity = scalar(@nodes);

    if ($quantity == 0) {
        @nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @nodes = ($nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @nodes = ($nodes[$quantity - 1]); }

    #Update element(s)
    my $parser = $self->{_xml_parser};
    foreach my $element (@nodes) {
        my @attributelist = $element->attributes();

        #Create the replacement element
        my $new_element = $doc->createElement($element->nodeName);
        my $content     = $parser->parse_balanced_chunk($self->opts->{element_new_value});
        $new_element->appendChild($content);

        foreach my $attribute (@attributelist) {
            $new_element->setAttribute($attribute->name, $attribute->value);

        }

        if ($element->toString(1) ne $root->toString(1)) {
            $element->replaceNode($new_element);
        }
        else {
            $root->replaceNode($new_element);
        }
    }
    print $doc->toString(1);

    #Save results
    $self->save_xml($source, $doc);

    return;
}

###########################################################################

=head2 update_attribute
 
  Title    : update_attribute
  Usage    : $self->update_attribute();
  Function : Update an xml attribute value from an element in a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub update_attribute {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $source = $self->opts->{source};
    my $query  = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Get the root
    my $root = $doc->getDocumentElement;

    #Execute xPath
    my @elements_nodes = $doc->findnodes($query);
    my $quantity       = scalar(@elements_nodes);

    if ($quantity == 0) {
        @elements_nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @elements_nodes = ($elements_nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @elements_nodes = ($elements_nodes[$quantity - 1]); }

    #Update attribute(s)
    foreach my $element (@elements_nodes) {
        if ($element->hasAttribute($self->opts->{attribute_name})) {

            #Update the new attribute
            $element->setAttribute($self->opts->{attribute_name}, $self->opts->{attribute_value});
        }
        else {
            $self->debug_msg(0, 'Attribute \'' . $self->opts->{attribute_name} . '\' not found.');
        }
    }
    print $doc->toString(1);

    #Save results
    $self->save_xml($source, $doc);

    return;
}

###########################################################################

=head2 extract_element
 
  Title    : extract_element
  Usage    : $self->extract_element();
  Function : Extract an xml element from a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub extract_element {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $prop   = $self->opts->{element_outpp};
    my $type   = "to_literal";
    my $source = $self->opts->{source};
    my $query  = $self->opts->{read_query};

    #Get XML
    my $doc = $self->get_xml($source);

    #Execute xPath
    my @nodes    = $doc->findnodes($query);
    my $quantity = scalar(@nodes);

    if ($quantity == 0) {
        @nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @nodes = ($nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @nodes = ($nodes[$quantity - 1]); }

    # Set the return type
    if ($self->opts->{return_type} eq 'tag') { $type = "toString"; }
    if ($self->opts->{selection}   eq 'all') { $prop = $self->opts->{element_outpsp}; }

    #Print and/or save results
    my $index = 1;
    foreach my $element (@nodes) {
        print $element->$type . "\n\n";
        if ($prop ne EMPTY) {
            if ($self->opts->{selection} eq 'all') {
                my $tag_name = $element->toString;
                $tag_name =~ /<([A-Za-z0-9]+).*/;
                $self->myProp->setProp($self->opts->{element_outpsp} . "/$1_$index", $element->$type);
            }
            else {
                $self->myProp->setProp($self->opts->{element_outpp}, $element->$type);
            }
        }

        my $parent_node = $element->parentNode;
        $parent_node->removeChild($element);

        $index++;
    }

    #Save results
    $self->save_xml($source, $doc);

    return;
}

###########################################################################

=head2 extract_attribute
 
  Title    : extract_attribute
  Usage    : $self->extract_attribute();
  Function : Extract an xml attribute from an element in a given xml text.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub extract_attribute {
    my $self = shift;

    $self->initialize();
    $self->debug_msg(1, '---------------------------------------------------------------------');
    if ($self->opts->{exitcode}) { return; }

    $self->debug_msg(1, 'Source: ' . $self->opts->{source});
    $self->debug_msg(1, 'Query: ' . $self->opts->{read_query});
    $self->debug_msg(1, 'Selection: ' . $self->opts->{selection});
    $self->debug_msg(1, '---------------------------------------------------------------------');

    my $prop      = $self->opts->{attribute_outpp};
    my $type      = "to_literal";
    my $source    = $self->opts->{source};
    my $attribute = $self->opts->{attribute};

    if ($self->opts->{read_query} !~ m/.+\@$attribute$/imgs) {
        $self->opts->{read_query} .= "/\@$attribute";
    }

    my $query  = $self->opts->{read_query};
    my $parser = $self->{_xml_parser};

    #Get XML
    my $doc = $self->get_xml($source);

    #Execute xPath
    my @nodes    = $doc->findnodes($query);
    my $quantity = scalar(@nodes);

    if ($quantity == 0) {
        @nodes = ();
        $self->debug_msg(0, 'ERROR: Query: \'' . $self->opts->{read_query} . '\' did not return values.');
        $self->opts->{exitcode} = ERROR;
        return;
    }

    # Set the selection of elements
    if    ($self->opts->{selection} eq 'first') { @nodes = ($nodes[0]); }
    elsif ($self->opts->{selection} eq 'last')  { @nodes = ($nodes[$quantity - 1]); }

    # Set the return type
    if ($self->opts->{return_type} eq 'structure') { $type = "toString"; }
    if ($self->opts->{selection}   eq 'all')       { $prop = $self->opts->{attribute_outpsp}; }

    #Print and/or save results
    my $index = 1;
    foreach my $element (@nodes) {
        print $element->$type . "\n";

        if ($prop ne EMPTY) {
            if ($self->opts->{selection} eq 'all') {
                $self->myProp->setProp($self->opts->{attribute_outpsp} . "/$attribute\_$index", $element->$type);
            }
            else {
                $self->myProp->setProp($self->opts->{attribute_outpp}, $element->$type);
            }
        }
        my $parent_node = $element->getOwnerElement();
        $parent_node->removeAttribute($element->name);

        $index++;
    }

    print $doc->toString(1);

    #Save results
    $self->save_xml($source, $doc);

    return;
}

# -------------------------------------------------------------------------
# Helper functions
# -------------------------------------------------------------------------

###########################################################################

=head2 myCmdr
 
  Title    : myCmdr
  Usage    : $self->myCmdr();
  Function : Get ElectricCommander instance.
  Returns  : ElectricCommander instance asociated to XMLEditDriver
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub myCmdr {
    my ($self) = @_;
    return $self->{_cmdr};
}

###########################################################################

=head2 opts
 
  Title    : opts
  Usage    : $self->opts();
  Function : Get opts hash.
  Returns  : opts hash
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub opts {
    my ($self) = @_;
    return $self->{_opts};
}

###########################################################################

=head2 initialize
 
  Title    : initialize
  Usage    : $self->initialize();
  Function : Set initial values.
  Returns  : none
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub initialize {
    my ($self) = @_;

    binmode STDOUT, ':encoding(utf8)';
    binmode STDIN,  ':encoding(utf8)';
    binmode STDERR, ':encoding(utf8)';

    $self->{_props} = ElectricCommander::PropDB->new($self->myCmdr(), EMPTY);

    # Set defaults
    if (!defined($self->opts->{debug})) {
        $self->opts->{debug} = DEFAULT_DEBUG;
    }

    $self->opts->{exitcode} = SUCCESS;
    $self->opts->{JobId}    = $ENV{COMMANDER_JOBID};

    return;
}

###########################################################################

=head2 myProp
 
  Title    : myProp
  Usage    : $self->myProp();
  Function : Get PropDB.
  Returns  : PropDB
  Args     : named arguments:
           : none
           :
=cut

###########################################################################
sub myProp {
    my ($self) = @_;
    return $self->{_props};
}

###########################################################################

=head2 setProp
 
  Title    : setProp
  Usage    : $self->setProp();
  Function : Use stored property prefix and PropDB to set a property.
  Returns  : setResult => result returned by PropDB->setProp
  Args     : named arguments:
           : -location => relative location to set the property
           : -value    => value of the property
           :
=cut

###########################################################################
sub setProp {
    my ($self, $location, $value) = @_;
    my $setResult = $self->myProp->setProp($self->opts->{PropPrefix} . $location, $value);
    return $setResult;
}

###########################################################################

=head2 getProp
 
  Title    : getProp
  Usage    : $self->getProp();
  Function : Use stored property prefix and PropDB to get a property.
  Returns  : getResult => property value
  Args     : named arguments:
           : -location => relative location to get the property
           :
=cut

###########################################################################
sub getProp {
    my ($self, $location) = @_;
    my $getResult = $self->myProp->getProp($self->opts->{PropPrefix} . $location);
    return $getResult;
}

###########################################################################

=head2 debug_msg
 
  Title    : debug_msg
  Usage    : $self->debug_msg();
  Function : Print a debug message.
  Returns  : none
  Args     : named arguments:
           : -errorlevel => number compared to $self->opts->{debug}
           : -msg        => string message
           :
=cut

###########################################################################
sub debug_msg {
    my ($self, $errlev, $msg) = @_;
    if ($self->opts->{debug} >= $errlev) { print "$msg\n"; }
    return;
}

###########################################################################

=head2 get_xml
 
  Title    : get_xml
  Usage    : $self->get_xml();
  Function : Parse the xml according to the source.
  Returns  : doc => parsed xml.
  Args     : named arguments:
           : -source => type of source to get xml
           :
=cut

###########################################################################
sub get_xml {
    my $self   = shift;
    my $source = shift;

    my $xml;
    my $doc;
    my $parser = $self->{_xml_parser};

    #Parse the xml according to the source.
    if ($source eq q{filepath}) {
        $doc = $parser->parse_file($self->opts->{filepath});
    }
    elsif ($source eq q{property_path}) {
        $xml = $self->myProp->getProp($self->opts->{property_path});
        $doc = $parser->parse_string($xml);
    }
    else {
        $xml = $self->opts->{xml_code};
        $doc = $parser->parse_string($xml);
    }

    return $doc;
}

###########################################################################

=head2 save_xml
 
  Title    : save_xml
  Usage    : $self->save_xml();
  Function : Save the xml according to the source.
  Returns  : none
  Args     : named arguments:
           : -source => type of source to get xml
           : -doc    => xml object
           :
=cut

###########################################################################
sub save_xml {
    my $self   = shift;
    my $source = shift;
    my $doc    = shift;

    #Save results
    if ($source eq q{filepath}) {
        open my $out, q{>}, $self->opts->{filepath} or croak "Unable to open " . $self->opts->{filepath} . " $!";
        binmode $out;
        print {$out} $doc->toString(1);
        close $out or croak "Unable to close " . $self->opts->{filepath} . " $!";
    }
    elsif ($source eq q{property_path}) {
        $self->myProp->setProp($self->opts->{property_path}, $doc->toString(1));

    }
    else {
        if (defined($self->opts->{xml_outpp}) && $self->opts->{xml_outpp} ne EMPTY) {
            $self->myProp->setProp($self->opts->{xml_outpp}, $doc->toString(1));
        }
    }
    return;
}

1;
