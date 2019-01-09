##########################
# extractElement.pl
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

$opts->{source}         = ($ec->getProperty("source"))->findvalue('//value')->string_value;
$opts->{filepath}       = ($ec->getProperty("filepath"))->findvalue('//value')->string_value;
$opts->{property_path}  = ($ec->getProperty("property_path"))->findvalue('//value')->string_value;
$opts->{xml_code}       = ($ec->getProperty("xml_code"))->findvalue('//value')->string_value;
$opts->{read_query}     = ($ec->getProperty("read_query"))->findvalue('//value')->string_value;
$opts->{return_type}    = ($ec->getProperty("return_type"))->findvalue('//value')->string_value;
$opts->{selection}      = ($ec->getProperty("selection"))->findvalue('//value')->string_value;
$opts->{element_outpp}  = ($ec->getProperty("element_outpp"))->findvalue('//value')->string_value;
$opts->{element_outpsp} = ($ec->getProperty("element_outpsp"))->findvalue('//value')->string_value;
$opts->{xml_outpp}      = ($ec->getProperty("xml_outpp"))->findvalue('//value')->string_value;

$[/myProject/procedure_helpers/preamble]

$xmledit->extract_element();
exit($opts->{exitcode});
