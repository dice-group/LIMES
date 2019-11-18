var mathChangeJson = {
  "message0": "change %1 by %2",
  "args0": [
    {"type": "field_variable", "name": "VAR", "variable": "item", "variableTypes": [""]},
    {"type": "input_value", "name": "DELTA", "check": "Number"}
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 230
};

var Start = {
  "type": "start",
  "message0": "Start %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "Measure",
        "Operator"
      ]
    }
  ],
  "colour": 0,
  "tooltip": "",
  "helpUrl": ""
}

var emptyBlock = {
  "type": "emptyBlock",
  "message0": "%1",
  "args0": [
    {
      "type": "input_dummy"
    }
  ],
  "output": [
    "emptyBlock",
  ],
  "colour": 105,
  "tooltip": "",
  "helpUrl": ""
}

var propertyPath = {
  "type": "propertyPath",
  "message0": "%1 %2 %3 %4",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "path",
      "options": [
        [
          "/",
          "sslash"
        ],
        [
          "\\",
          "bslash"
        ]
      ]
    },
    {
      "type": "field_dropdown",
      "name": "propTitle",
      "options": [
        [
          "",
          ""
        ],
      ]
    },
    {
      "type": "field_checkbox",
      "name": "enable_propertypath",
      "checked": false
    },
    {
      "type": "input_value",
      "name": "propertyPath",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "PreprocessingFunction",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "emptyBlock",
        "propertyPath",
      ]
    }
  ],
  "output": "propertyPath",
  "colour": 150,
  "tooltip": "propertyPath",
  "helpUrl": ""
}


var sourceProperty = {
  "type": "sourceproperty",
  "message0": "Source property %1 PP %2 %3",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "propTitle",
      "options": [
        [
          "",
          ""
        ]
      ]
    },
    {
      "type": "field_checkbox",
      "name": "enable_propertypath",
      "checked": false
    },
    {
      "type": "input_value",
      "name": "propName",
      "check": [
        "emptyBlock",  
        "propertyPath",    
      ]
    }
  ],
  "output": "SourceProperty",
  "colour": 105,
  "tooltip": "Source property block",
  "helpUrl": ""
}

var targetProperty = {
  "type": "targetproperty",
  "message0": "Target property %1 PP %2 %3",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "propTitle",
      "options": [
        [
          "",
          ""
        ]
      ]
    },
    {
      "type": "field_checkbox",
      "name": "enable_propertypath",
      "checked": false
    },
    {
      "type": "input_value",
      "name": "propName",
      "check": [
        "emptyBlock",  
        "propertyPath",    
      ]
    }
  ],
  "output": "TargetProperty",
  "colour": 225,
  "tooltip": "Source property block",
  "helpUrl": ""
}

var optionalSourceProperty = {
  "type": "optionalsourceproperty",
  "message0": "Optional source property %1 PP %2 %3",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "propTitle",
      "options": [
        [
          "",
          ""
        ]
      ]
    },    
    {
      "type": "field_checkbox",
      "name": "enable_propertypath",
      "checked": false
    },
    {
      "type": "input_value",
      "name": "propName",
      "check": [
        "emptyBlock",  
        "propertyPath",    
      ]
    }
  ],
  "output": "OptionalSourceProperty",
  "colour": 80,
  "tooltip": "Optional source property block",
  "helpUrl": ""
}

var optionalTargetProperty = {
  "type": "optionaltargetproperty",
  "message0": "Optional target property %1 PP %2 %3",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "propTitle",
      "options": [
        [
          "",
          ""
        ]
      ]
    },
    {
      "type": "field_checkbox",
      "name": "enable_propertypath",
      "checked": false
    },
    {
      "type": "input_value",
      "name": "propName",
      "check": [
        "emptyBlock",  
        "propertyPath",    
      ]
    }
  ],
  "output": "OptionalTargetProperty",
  "colour": 215,
  "tooltip": "Optional target property block",
  "helpUrl": ""
}

var RenamePreprocessingFunction = {
  "type": "renamepreprocessingfunction",
  "message0": "Rename %1 As %2",
  "args0": [
    {
      "type": "input_value",
      "name": "RENAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "PreprocessingFunction",
        "OptionalTargetProperty",
        "OptionalSourceProperty"
      ]
    },
    {
      "type": "field_input",
      "name": "RENAME",
      "text": "X"
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 300,
  "tooltip": "Pre-processing function",
  "helpUrl": ""
}

var ComplexPreprocessingFunction = {
  "type": "complexpreprocessingfunction",
  "message0": "%1 %2 %3 %4 %5 Rename %6 %7 %8 %9 %10",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "complexfunction",
      "options": [
        [
          "Concat",
          "concat"
        ],
        [
          "Split",
          "split"
        ],
        [
          "ToWktPoint",
          "towktpoint"
        ]
      ]
    },
    {
      "type": "input_value",
      "name": "Prop1",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty"
      ]
    },
    {
      "type": "input_value",
      "name": "Prop2",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty"
      ]
    },
    {
      "type": "field_checkbox",
      "name": "enable_glue",
      "checked": false
    },
    {
      "type": "field_input",
      "name": "glue_text",
      "text": ","
    },
    {
      "type": "field_checkbox",
      "name": "enable_A",
      "checked": true
    },
    {
      "type": "field_input",
      "name": "RENAME1",
      "text": "A"
    },
    {
      "type": "field_checkbox",
      "name": "enable_B",
      "checked": false
    },
    {
      "type": "field_input",
      "name": "RENAME2",
      "text": "B"
    },
    {
      "type": "input_dummy",
      "name": "params"
    }

  ],
  "output": "PreprocessingFunction",
  "colour": 300,
  "tooltip": "Pre-processing function",
  "helpUrl": ""
}

var PreprocessingFunction = {
  "type": "preprocessingfunction",
  "message0": "%1 %2 %3 %4",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "function",
      "options": [
        [
          "Select Preprocessing Function",
          "selectPreprocessingFunction"
        ],
      ]
    },
    {
      "type": "field_input",
      "name": "textA",
      "text": "A"
    },
    {
      "type": "field_input",
      "name": "textB",
      "text": "B"
    },
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"
      ]
    },
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var LowercasePreprocessingFunction = {
  "type": "lowercasepreprocessingfunction",
  "message0": "Lowercase %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var UppercasePreprocessingFunction = {
  "type": "uppercasepreprocessingfunction",
  "message0": "Uppercase %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var NumberPreprocessingFunction = {
  "type": "numberpreprocessingfunction",
  "message0": "Number %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var NolangPreprocessingFunction = {
  "type": "nolangpreprocessingfunction",
  "message0": "Nolang %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"        
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var CleaniriPreprocessingFunction = {
  "type": "cleaniripreprocessingfunction",
  "message0": "Cleaniri %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"        
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var CelsiusPreprocessingFunction = {
  "type": "celsiuspreprocessingfunction",
  "message0": "Celsius %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"        
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var FahrenheitPreprocessingFunction = {
  "type": "fahrenheitpreprocessingfunction",
  "message0": "Fahrenheit %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"        
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var RemovebracesPreprocessingFunction = {
  "type": "removebracespreprocessingfunction",
  "message0": "Removebraces %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"        
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var RegularAlphabetPreprocessingFunction = {
  "type": "regularalphabetpreprocessingfunction",
  "message0": "RegularAlphabet %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"        
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var UriasstringPreprocessingFunction = {
  "type": "uriasstringpreprocessingfunction",
  "message0": "Uriasstring %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty",
        "OptionalTargetProperty",
        "OptionalSourceProperty",
        "PreprocessingFunction"        
      ]
    }
  ],
  "output": "PreprocessingFunction",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var Measure = {
  "type": "measure",
  "message0": "%1 Threshold %2 %3 %4 Source property  %5 Target property  %6",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "measureList",
      "options": [
        [
          "Cosine",
          "cos"
        ],
        [
          "Jaccard",
          "jac"
        ],
        [
          "Overlap",
          "ovr"
        ],
        [
          "ExactMatch",
          "ext"
        ]
      ]
    },
    {
      "type": "field_checkbox",
      "name": "enable_threshold",
      "checked": false
    },
    {
      "type": "field_number",
      "name": "threshold",
      "value": 0.5,
      "min": 0,
      "max": 1,
      "precision": 1e-8
    },
    {
      "type": "input_dummy"
    },
    {
      "type": "input_value",
      "name": "sourceProperty",
      "check": [
        "SourceProperty",
        "PreprocessingFunction", 
        "OptionalSourceProperty"     
      ]
    },
    {
      "type": "input_value",
      "name": "targetProperty",
      "check": [
        "TargetProperty",
        "PreprocessingFunction",
        "OptionalTargetProperty"
      ]
    }
  ],
  "output": "Measure",
  "colour": 240,
  "tooltip": "Measure block",
  "helpUrl": ""
}

var Operator = {
  "type": "operator",
  "message0": "Operator %1 %2 %3",
  "args0": [
    {
      "type": "field_dropdown",
      "name": "operators",
      "options": [
        [
          "AND",
          "and"
        ],
        [
          "OR",
          "or"
        ],
        [
          "MINUS",
          "minus"
        ],
        [
          "XOR",
          "xor"
        ]
      ]
    },
    {
      "type": "input_value",
      "name": "rename",
      "check": [
        "Measure",
        "Operator"
      ]
    },
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "Measure",
        "Operator"
      ]
    }
  ],
  "output": "Operator",
  "colour": 20,
  "tooltip": "Operator",
  "helpUrl": ""
}

let MLParameters = {
	"WOMBAT": {
		"max refinement tree size": { "default": 2000 },
		"max iterations number": { "default": 3 },
		"max iteration time in minutes": { "default": 20 },
		"max execution time in minutes": { "default": 600 },
		"max fitness threshold": { "default": 1, "min": 0, "max": 1 },
		"minimum property coverage": { "default": 0.4, "min": 0, "max": 1 },
		"property learning rate": { "default": 0.9, "min": 0, "max": 1 },
		"overall penalty weight": { "default": 0.5, "min": 0, "max": 1 },
		"children penalty weight": { "default": 1, "min": 0, "max": 1 },
		"complexity penalty weight": { "default": 1, "min": 0, "max": 1 },
		"beta": { "default": 1, "min": 0, "max": Number.MAX_VALUE },
		"verbose": false,
		"atomic measures": { "default": "jaccard, trigrams, cosine, qgrams"},
		"save mapping": true,
	},
	"EAGLE": {},
}
