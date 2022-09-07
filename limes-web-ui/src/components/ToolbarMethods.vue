<template>

</template>

<script>
import ConfigurationFileParser from './ConfigurationFileParser.vue'
export default {
  mixins: [ConfigurationFileParser],
  props: {
  },
  methods:{
    exampleConfig() {
      this.exampleConfigEnable = true;
      this.prefixes = [
        {
          namespace: 'http://geovocab.org/geometry#',
          label: 'ngeo',
        },
        {
          namespace: 'http://www.opengis.net/ont/geosparql#',
          label: 'geos',
        },
        {
          namespace: 'http://linkedgeodata.org/ontology/',
          label: 'lgdo',
        },
      ];
       let source = {
        id: 'sourceId',
        endpoint: 'http://linkedgeodata.org/sparql',
        var: '?s',
        pagesize: 2000,
        restriction: '?s a lgdo:RelayBox',
        type: '',
        properties: ['ngeo:geometry/geos:asWKT RENAME GEOMETRY', 'ngeo:geometry AS uriasstring RENAME STRGEO'],
        optionalProperties: [],
        classes: ['http://linkedgeodata.org/ontology/RelayBox'],
        propertiesForChoice: [],
      };
      let target = {
        id: 'targetId',
        endpoint: 'http://linkedgeodata.org/sparql',
        var: '?t',
        pagesize: 2000,
        restriction: '?t a lgdo:RelayBox',
        type: '',
        properties: ['ngeo:geometry/geos:asWKT RENAME GEOMETRY', 'ngeo:geometry AS uriasstring RENAME STRGEO'],
        optionalProperties: [],
        classes: ['http://linkedgeodata.org/ontology/RelayBox'],
        propertiesForChoice: [],
      };
      this.$store.commit('changeSource', source);
      this.$store.commit('changeTarget', target);
      this.importWorkspaceString = `
      <xml xmlns="http://www.w3.org/1999/xhtml">
  <block type="start" id="]~iOOuxgG1il)Qn#!5@R" deletable="false" x="-43" y="223">
    <value name="NAME">
      <block type="operator" id="UPt),ECc.Dk11X$cW=xM">
        <field name="operators">minus</field>
        <value name="rename">
          <block type="measure" id="KV+e%A!n/j5k2T@D@*GH">
            <field name="measureList">Geo_Hausdorff</field>
            <field name="enable_threshold">TRUE</field>
            <field name="threshold">0.5</field>
            <value name="sourceProperty">
              <block type="renamepreprocessingfunction" id="$J8bghZmrI;ETkue6mwG">
                <field name="RENAME">GEOMETRY</field>
                <value name="RENAME">
                  <block type="sourceproperty" id="sp">
                    <field name="propTitle">ngeo:geometry</field>
                    <field name="enable_propertypath">TRUE</field>
                    <value name="propName">
                      <block type="propertyPath" id="KJ" movable="false">
                        <field name="path">sslash</field>
                        <field name="propTitle">geos:asWKT</field>
                        <field name="enable_propertypath">FALSE</field>
                        <value name="propertyPath">
                          <block type="emptyBlock" id="sC" movable="false"></block>
                        </value>
                      </block>
                    </value>
                  </block>
                </value>
              </block>
            </value>
            <value name="targetProperty">
              <block type="renamepreprocessingfunction" id="rpf">
                <field name="RENAME">GEOMETRY</field>
                <value name="RENAME">
                  <block type="targetproperty" id="n1O[#^wbmB=cRY2:Dh!z">
                    <field name="propTitle">ngeo:geometry</field>
                    <field name="enable_propertypath">TRUE</field>
                    <value name="propName">
                      <block type="propertyPath" id="%+^C#EQ0a;_--$5EeDgs" movable="false">
                        <field name="path">sslash</field>
                        <field name="propTitle">geos:asWKT</field>
                        <field name="enable_propertypath">FALSE</field>
                        <value name="propertyPath">
                          <block type="emptyBlock" id="8GKMIIbCGbtv##3I;,Cb" movable="false"></block>
                        </value>
                      </block>
                    </value>
                  </block>
                </value>
              </block>
            </value>
          </block>
        </value>
        <value name="NAME">
          <block type="measure" id="wmW/JW:xDV_!PVlXFa|(">
            <field name="measureList">ExactMatch</field>
            <field name="enable_threshold">TRUE</field>
            <field name="threshold">0.5</field>
            <value name="sourceProperty">
              <block type="renamepreprocessingfunction" id=")dz~dGl1q%Xg{C%6#LRu">
                <field name="RENAME">STRGEO</field>
                <value name="RENAME">
                  <block type="preprocessingfunction" id="?l4-Mzuk2x_3YD#$CXKp">
                    <field name="function">uriasstring</field>
                    <value name="NAME">
                      <block type="sourceproperty" id=":88(wrdI!6+RK^8!MQ(">
                        <field name="propTitle">ngeo:geometry</field>
                        <field name="enable_propertypath">FALSE</field>
                        <value name="propName">
                          <block type="emptyBlock" id="XDA*vb;FzNgxwRjEo+t}" movable="false"></block>
                        </value>
                      </block>
                    </value>
                  </block>
                </value>
              </block>
            </value>
            <value name="targetProperty">
              <block type="renamepreprocessingfunction" id="!#=KZssUMJ?pXPUG%F6{">
                <field name="RENAME">STRGEO</field>
                <value name="RENAME">
                  <block type="preprocessingfunction" id="v74wNGJcreym+@=B[Zk=">
                    <field name="function">uriasstring</field>
                    <value name="NAME">
                      <block type="targetproperty" id="O}%Ewti;W(-qMNU0ssXm">
                        <field name="propTitle">ngeo:geometry</field>
                        <field name="enable_propertypath">FALSE</field>
                        <value name="propName">
                          <block type="emptyBlock" id="Q?K%^jF8k3(y5nqD[MYd" movable="false"></block>
                        </value>
                      </block>
                    </value>
                  </block>
                </value>
              </block>
            </value>
          </block>
        </value>
      </block>
    </value>
  </block>
</xml>
`;
      this.xmlToWorkspace(this.importWorkspaceString);
      this.metrics = ['MINUS(geo_hausdorff(s.GEOMETRY,t.GEOMETRY)|0.5,exactmatch(s.STRGEO,t.STRGEO)|0.5)'];
      this.acceptance = {
        id: 'acceptance',
        threshold: 0.9,
        file: 'lgd_relaybox_verynear.nt',
        relation: 'lgdo:near',
      };
      this.review = {
        id: 'review',
        threshold: 0.5,
        file: 'lgd_relaybox_near.nt',
        relation: 'lgdo:near',
      };
      this.execution = {
        rewriter: 'DEFAULT',
        planner: 'DEFAULT',
        engine: 'DEFAULT',
      };
      this.output = {type: 'TAB'};
    },
    exampleFilmAndMovieConfig(){
      let textFromXMLFile = `<?xml version="1.0" encoding="UTF-8"?>
        <!DOCTYPE LIMES SYSTEM "limes.dtd">
        <LIMES>
        <PREFIX>
          <NAMESPACE>http://www.w3.org/2002/07/owl#</NAMESPACE>
          <LABEL>owl</LABEL>
        </PREFIX>
        <PREFIX>
          <NAMESPACE>http://schema.org/</NAMESPACE>
          <LABEL>url</LABEL>
        </PREFIX>
        <PREFIX>
          <NAMESPACE>http://www.w3.org/1999/02/22-rdf-syntax-ns#</NAMESPACE>
          <LABEL>rdf</LABEL>
        </PREFIX>
        <PREFIX>
          <NAMESPACE>http://dbpedia.org/ontology/</NAMESPACE>
          <LABEL>dbpo</LABEL>
        </PREFIX>
        <PREFIX>
          <NAMESPACE>http://www.w3.org/2000/01/rdf-schema#</NAMESPACE>
          <LABEL>rdfs</LABEL>
        </PREFIX>
        <SOURCE>
          <ID>sourceId</ID>
          <ENDPOINT>http://dbpedia.org/sparql</ENDPOINT>
          <VAR>?s</VAR>
          <PAGESIZE>1000</PAGESIZE>
          <RESTRICTION>?s rdf:type url:Movie</RESTRICTION>
          <PROPERTY>rdfs:label</PROPERTY>

          <TYPE>sparql</TYPE>
        </SOURCE>
        <TARGET>
          <ID>targetId</ID>
          <ENDPOINT>http://dbpedia.org/sparql</ENDPOINT>
          <VAR>?t</VAR>
          <PAGESIZE>1000</PAGESIZE>
          <RESTRICTION>?t rdf:type dbpo:Film</RESTRICTION>
          <PROPERTY>rdfs:label</PROPERTY>

          <TYPE>sparql</TYPE>
        </TARGET>
        <METRIC>
          AND(cosine(s.rdfs:label,t.rdfs:label)|0.9,exactmatch(s.rdfs:label,t.rdfs:label)|0.9)
        </METRIC>
        <ACCEPTANCE>
        <THRESHOLD>0.98</THRESHOLD>
        <FILE>accepted.nt</FILE>
        <RELATION>owl:sameAs</RELATION>
        </ACCEPTANCE>
        <REVIEW>
        <THRESHOLD>0.9</THRESHOLD>
        <FILE>reviewme.nt</FILE>
        <RELATION>owl:sameAs</RELATION>
        </REVIEW>
        <EXECUTION>
          <REWRITER>DEFAULT</REWRITER>
          <PLANNER>DEFAULT</PLANNER>
          <ENGINE>DEFAULT</ENGINE>
        </EXECUTION>
        <OUTPUT>TAB</OUTPUT>
      </LIMES>`;
      this.xmlToHtml(textFromXMLFile);
    },   
    importConfigurationFile(){
      this.importConfigurationFileFunction(); 
    }, 
  }
}
</script>
