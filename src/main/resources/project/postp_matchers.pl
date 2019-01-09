use ElectricCommander;

push(
    @::gMatchers,
    {
       id      => "wrong_query",
       pattern => q{Query:\s\'(.+)\'\sdid\snot\sreturn\svalues},
       action  => q{
                               my $desc = ((defined $::gProperties{"summary"}) ? $::gProperties{"summary"} : '');
                               $desc .= "Query: \'$1\' did not return values";
                               setProperty("summary", $desc . "\n");
                               setProperty("/myJobStep/outcome", 'error');
                             },
    },

    {
       id      => "empty_xml",
       pattern => q{Empty\sString.+},
       action  => q{
                               my $desc = ((defined $::gProperties{"summary"}) ? $::gProperties{"summary"} : '');
                               $desc .= "Empty XML";
                               setProperty("summary", $desc . "\n");
                               setProperty("/myJobStep/outcome", 'error');
                             },
    },

    {
       id      => "file",
       pattern => q{^Could\snot\screate\sfile.+for\sfile\s(.+):},
       action  => q{    
                               my $desc = ((defined $::gProperties{"summary"}) ? $::gProperties{"summary"} : '');
                               $desc .= "Could not load file: $1";
                               setProperty("summary", $desc . "\n");
                               setProperty("/myJobStep/outcome", 'error');

                             },
    },                              
);

