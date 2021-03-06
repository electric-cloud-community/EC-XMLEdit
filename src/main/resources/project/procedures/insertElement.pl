##########################
# insertElement.pl
##########################
use warnings;
use strict;
use Encode;
use utf8;
use open IO => ':encoding(utf8)';

## Create ElectricCommander instance
my $ec = new ElectricCommander();
$ec->abortOnError(0);

my $opts;

$opts->{filepath}      = ($ec->getProperty("filepath"))->findvalue('//value')->string_value;
$opts->{property_path} = ($ec->getProperty("property_path"))->findvalue('//value')->string_value;
$opts->{read_query}    = ($ec->getProperty("read_query"))->findvalue('//value')->string_value;
$opts->{source}        = ($ec->getProperty("source"))->findvalue('//value')->string_value;
$opts->{xml_code}      = ($ec->getProperty("xml_code"))->findvalue('//value')->string_value;
$opts->{element}       = ($ec->getProperty("element"))->findvalue('//value')->string_value;
$opts->{element_tag}   = ($ec->getProperty("element_tag"))->findvalue('//value')->string_value;
$opts->{selection}     = ($ec->getProperty("selection"))->findvalue('//value')->string_value;
$opts->{xml_outpp}     = ($ec->getProperty("xml_outpp"))->findvalue('//value')->string_value;

$[/myProject/procedure_helpers/preamble]

$xmledit->insert_element();
exit($opts->{exitcode});
