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


var sourceProperty = {
  "type": "sourceproperty",
  "message0": "Source property %1",
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
    }
  ],
  "output": "SourceProperty",
  "colour": 105,
  "tooltip": "Source property block",
  "helpUrl": ""
}

var targetProperty = {
  "type": "targetproperty",
  "message0": "Target property %1",
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
    }
  ],
  "output": "TargetProperty",
  "colour": 225,
  "tooltip": "Source property block",
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
        "PreprocessingFunction"
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

var LowercasePreprocessingFunction = {
  "type": "lowercasepreprocessingfunction",
  "message0": "Lowercase %1",
  "args0": [
    {
      "type": "input_value",
      "name": "NAME",
      "check": [
        "SourceProperty",
        "TargetProperty"
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
        "PreprocessingFunction"
      ]
    },
    {
      "type": "input_value",
      "name": "targetProperty",
      "check": [
        "TargetProperty",
        "PreprocessingFunction"
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
          "NOR",
          "xor"
        ],
        [
          "NAND",
          "nand"
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