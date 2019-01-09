my %xPathQuery = (
                  label       => "XMLEdit - Execute XPath Query",
                  procedure   => "XPathQuery",
                  description => "Execute an xpath query on a given xml text.",
                  category    => "Utility"
                 );

my %readElement = (
                   label       => "XMLEdit - Read XML Element",
                   procedure   => "ReadElement",
                   description => "Read the value of an xml element from a given xml text.",
                   category    => "Utility"
                  );

my %readAttribute = (
                     label       => "XMLEdit - Read XML Attribute",
                     procedure   => "ReadAttribute",
                     description => "Read the value of an xml attribute from a given xml text.",
                     category    => "Utility"
                    );

my %insertElement = (
                     label       => "XMLEdit - Insert an XML Element",
                     procedure   => "InsertElement",
                     description => "Insert an xml element on a given xml text.",
                     category    => "Utility"
                    );

my %insertAttribute = (
                       label       => "XMLEdit - Insert an XML Attribute",
                       procedure   => "InsertAttribute",
                       description => "Insert an xml attribute in an element on a given xml text.",
                       category    => "Utility"
                      );

my %deleteElement = (
                     label       => "XMLEdit - Delete an XML Element",
                     procedure   => "DeleteElement",
                     description => "Delete an xml element from a given xml text.",
                     category    => "Utility"
                    );

my %deleteAttribute = (
                       label       => "XMLEdit - Delete an XML Attribute",
                       procedure   => "DeleteAttribute",
                       description => "Delete an xml attribute from an element in a given xml text.",
                       category    => "Utility"
                      );

my %updateElement = (
                     label       => "XMLEdit - Update an XML Element",
                     procedure   => "UpdateElement",
                     description => "Update an xml element from a given xml text.",
                     category    => "Utility"
                    );

my %updateAttribute = (
                       label       => "XMLEdit - Update an XML Attribute",
                       procedure   => "UpdateAttribute",
                       description => "Update an xml attribute value from an element in a given xml text.",
                       category    => "Utility"
                      );

my %extractElement = (
                      label       => "XMLEdit - Extract an XML Element",
                      procedure   => "ExtractElement",
                      description => "Extract an xml element from a given xml text.",
                      category    => "Utility"
                     );

my %extractAttribute = (
                        label       => "XMLEdit - Extract an XML Attribute",
                        procedure   => "ExtractAttribute",
                        description => "Extract an xml attribute value from an element in a given xml text.",
                        category    => "Utility"
                       );

$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Execute XPath Query");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Read XML Element");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Read XML Attribute");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Insert an XML Element");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Insert an XML Attribute");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Delete an XML Element");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Delete an XML Attribute");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Update an XML Element");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Update an XML Attribute");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Extract an XML Element");
$batch->deleteProperty("/server/ec_customEditors/pickerStep/XMLEdit - Extract an XML Attribute");

@::createStepPickerSteps = (\%xPathQuery, \%readElement, \%readAttribute, \%insertElement, \%insertAttribute, \%deleteElement, \%deleteAttribute, \%updateElement, \%updateAttribute, \%extractElement, \%extractAttribute);
