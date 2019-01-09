@files = (

    ['//property[propertyName="preamble"]/value',       'preamble.pl'],
    ['//property[propertyName="XMLEditDriver"]/value',  'XMLEditDriver.pm'],
    ['//property[propertyName="postp_matchers"]/value', 'postp_matchers.pl'],

    ['//property[propertyName="ec_setup"]/value', 'ec_setup.pl'],

    ['//step[stepName="XPathQuery"]/command',       'procedures/xPathQuery.pl'],
    ['//step[stepName="DeleteAttribute"]/command',  'procedures/deleteAttribute.pl'],
    ['//step[stepName="DeleteElement"]/command',    'procedures/deleteElement.pl'],
    ['//step[stepName="ExtractAttribute"]/command', 'procedures/extractAttribute.pl'],
    ['//step[stepName="ExtractElement"]/command',   'procedures/extractElement.pl'],
    ['//step[stepName="InsertAttribute"]/command',  'procedures/insertAttribute.pl'],
    ['//step[stepName="InsertElement"]/command',    'procedures/insertElement.pl'],
    ['//step[stepName="ReadAttribute"]/command',    'procedures/readAttribute.pl'],
    ['//step[stepName="ReadElement"]/command',      'procedures/readElement.pl'],
    ['//step[stepName="UpdateAttribute"]/command',  'procedures/updateAttribute.pl'],
    ['//step[stepName="UpdateElement"]/command',    'procedures/updateElement.pl'],

    ['//property[propertyName="ec_setup"]/value', 'ec_setup.pl'],

);
