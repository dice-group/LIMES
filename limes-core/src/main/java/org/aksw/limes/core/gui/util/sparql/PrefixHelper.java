package org.aksw.limes.core.gui.util.sparql;

import static org.aksw.limes.core.gui.util.sparql.PrefixHelper.LazyLoaded.prefixToURI;
import static org.aksw.limes.core.gui.util.sparql.PrefixHelper.LazyLoaded.uriToPrefix;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for prefixes in sparql queries
 * @author Daniel Obraczka {@literal <} soz11ffe{@literal @}
 *         studserv.uni-leipzig.de{@literal >}
 *
 */
public class PrefixHelper {
    /**
     * prefixes
     */
    final static String[][] prefixArray = {
            {"foaf", "http://xmlns.com/foaf/0.1/"},
            {"lgdo", "http://linkedgeodata.org/ontology/"},
            {"lgdp", "http://linkedgeodata.org/property/"},
            {"rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"},
            {"dc", "http://purl.org/dc/terms/"},
            {"rdfs", "http://www.w3.org/2000/01/rdf-schema#"},
            {"owl", "http://www.w3.org/2002/07/owl#"},
            {"geonames", "http://www.geonames.org/ontology#"},
            {"geo", "http://www.w3.org/2003/01/geo/wgs84_pos#"},
            {"skos", "http://www.w3.org/2004/02/skos/core#"},
            {"dbp", "http://dbpedia.org/property/"},
            {"swrc", "http://swrc.ontoware.org/ontology#"},
            {"sioc", "http://rdfs.org/sioc/ns#"},
            {"xsd", "http://www.w3.org/2001/XMLSchema#"},
            {"dbo", "http://dbpedia.org/ontology/"},
            {"dc11", "http://purl.org/dc/elements/1.1/"},
            {"doap", "http://usefulinc.com/ns/doap#"},
            {"dbpprop", "http://dbpedia.org/property/"},
            {"content", "http://purl.org/rss/1.0/modules/content/"},
            {"wot", "http://xmlns.com/wot/0.1/"},
            {"rss", "http://purl.org/rss/1.0/"},
            {"gen", "http://www.w3.org/2006/gen/ont#"},
            {"d2rq", "http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#"},
            {"dbpedia", "http://dbpedia.org/resource/"},
            {"nie", "http://www.semanticdesktop.org/ontologies/2007/01/19/nie#"},
            {"xhtml", "http://www.w3.org/1999/xhtml#"},
            {"test2", "http://this.invalid/test2#"},
            {"gr", "http://purl.org/goodrelations/v1#"},
            {"dcterms", "http://purl.org/dc/terms/"},
            {"akt", "http://www.aktors.org/ontology/portal#"},
            {"org", "http://www.w3.org/ns/org#"},
            {"vcard", "http://www.w3.org/2006/vcard/ns#"},
            {"dct", "http://purl.org/dc/terms/"},
            {"ex", "http://example.org/"},
            {"owlim", "http://www.ontotext.com/trree/owlim#"},
            {"fb", "http://rdf.freebase.com/ns/"},
            {"cfp", "http://sw.deri.org/2005/08/conf/cfp.owl#"},
            {"xf", "http://www.w3.org/2002/xforms/"},
            {"sism", "http://purl.oclc.org/NET/sism/0.1/"},
            {"earl", "http://www.w3.org/ns/earl#"},
            {"bio", "http://purl.org/vocab/bio/0.1/"},
            {"reco", "http://ontologies.ezweb.morfeo-project.org/reco/ns#"},
            {"media", "http://purl.org/microformat/hmedia/"},
            {"xfn", "http://vocab.sindice.com/xfn#"},
            {"dcmit", "http://purl.org/dc/dcmitype/"},
            {"fn", "http://www.w3.org/2005/xpath-functions#"},
            {"air", "http://dig.csail.mit.edu/TAMI/2007/amord/air#"},
            {"void", "http://rdfs.org/ns/void#"},
            {"afn", "http://jena.hpl.hp.com/ARQ/function#"},
            {"cc", "http://creativecommons.org/ns#"},
            {"cld", "http://purl.org/cld/terms/"},
            {"ical", "http://www.w3.org/2002/12/cal/ical#"},
            {"mu", "http://www.kanzaki.com/ns/music#"},
            {"vann", "http://purl.org/vocab/vann/"},
            {"days", "http://ontologi.es/days#"},
            {"http", "http://www.w3.org/2006/http#"},
            {"osag", "http://www.ordnancesurvey.co.uk/ontology/AdministrativeGeography/v2.0/AdministrativeGeography.rdf#"},
            {"cal", "http://www.w3.org/2002/12/cal/ical#"},
            {"sd", "http://www.w3.org/ns/sparql-service-description#"},
            {"musim", "http://purl.org/ontology/similarity/"},
            {"botany", "http://purl.org/NET/biol/botany#"},
            {"factbook", "http://www4.wiwiss.fu-berlin.de/factbook/ns#"},
            {"cs", "http://purl.org/vocab/changeset/schema#"},
            {"swande", "http://purl.org/swan/1.2/discourse-elements/"},
            {"rev", "http://purl.org/stuff/rev#"},
            {"log", "http://www.w3.org/2000/10/swap/log#"},
            {"bibo", "http://purl.org/ontology/bibo/"},
            {"cv", "http://rdfs.org/resume-rdf/"},
            {"ome", "http://purl.org/ontomedia/core/expression#"},
            {"biblio", "http://purl.org/net/biblio#"},
            {"ok", "http://okkam.org/terms#"},
            {"rel", "http://purl.org/vocab/relationship/"},
            {"giving", "http://ontologi.es/giving#"},
            {"dir", "http://schemas.talis.com/2005/dir/schema#"},
            {"memo", "http://ontologies.smile.deri.ie/2009/02/27/memo#"},
            {"ir", "http://www.ontologydesignpatterns.org/cp/owl/informationrealization.owl#"},
            {"event", "http://purl.org/NET/c4dm/event.owl#"},
            {"ad", "http://schemas.talis.com/2005/address/schema#"},
            {"af", "http://purl.org/ontology/af/"},
            {"dbr", "http://dbpedia.org/resource/"},
            {"co", "http://purl.org/ontology/co/core#"},
            {"xs", "http://www.w3.org/2001/XMLSchema#"},
            {"rif", "http://www.w3.org/2007/rif#"},
            {"daia", "http://purl.org/ontology/daia/"},
            {"swc", "http://data.semanticweb.org/ns/swc/ontology#"},
            {"bill", "http://www.rdfabout.com/rdf/schema/usbill/"},
            {"tag", "http://www.holygoat.co.uk/owl/redwood/0.1/tags/"},
            {"dcq", "http://purl.org/dc/terms/"},
            {"rdfg", "http://www.w3.org/2004/03/trix/rdfg-1/"},
            {"xhv", "http://www.w3.org/1999/xhtml/vocab#"},
            {"swanq", "http://purl.org/swan/1.2/qualifiers/"},
            {"cmp", "http://www.ontologydesignpatterns.org/cp/owl/componency.owl#"},
            {"aiiso", "http://purl.org/vocab/aiiso/schema#"},
            {"sr", "http://www.openrdf.org/config/repository/sail#"},
            {"math", "http://www.w3.org/2000/10/swap/math#"},
            {"book", "http://purl.org/NET/book/vocab#"},
            {"jdbc", "http://d2rq.org/terms/jdbc/"},
            {"ne", "http://umbel.org/umbel/ne/"},
            {"dcn", "http://www.w3.org/2007/uwa/context/deliverycontext.owl#"},
            {"myspace", "http://purl.org/ontology/myspace#"},
            {"ctag", "http://commontag.org/ns#"},
            {"os", "http://www.w3.org/2000/10/swap/os#"},
            {"tzont", "http://www.w3.org/2006/timezone#"},
            {"omb", "http://purl.org/ontomedia/ext/common/being#"},
            {"spc", "http://purl.org/ontomedia/core/space#"},
            {"lx", "http://purl.org/NET/lx#"},
            {"wn", "http://xmlns.com/wordnet/1.6/"},
            {"lang", "http://ontologi.es/lang/core#"},
            {"money", "http://purl.org/net/rdf-money/"},
            {"rep", "http://www.openrdf.org/config/repository#"},
            {"ore", "http://www.openarchives.org/ore/terms/"},
            {"coref", "http://www.rkbexplorer.com/ontologies/coref#"},
            {"dcam", "http://purl.org/dc/dcam/"},
            {"tdb", "http://jena.hpl.hp.com/2008/tdb#"},
            {"nfo", "http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#"},
            {"irrl", "http://www.ontologydesignpatterns.org/cp/owl/informationobjectsandrepresentationlanguages.owl#"},
            {"kwijibo", "http://kwijibo.talis.com/"},
            {"h5", "http://buzzword.org.uk/rdf/h5#"},
            {"atomix", "http://buzzword.org.uk/rdf/atomix#"},
            {"swrlb", "http://www.w3.org/2003/11/swrlb#"},
            {"con", "http://www.w3.org/2000/10/swap/pim/contact#"},
            {"lomvoc", "http://ltsc.ieee.org/rdf/lomv1p0/vocabulary#"},
            {"dctype", "http://purl.org/dc/dcmitype/"},
            {"spin", "http://spinrdf.org/spin#"},
            {"akts", "http://www.aktors.org/ontology/support#"},
            {"imm", "http://schemas.microsoft.com/imm/"},
            {"video", "http://purl.org/media/video#"},
            {"ti", "http://www.ontologydesignpatterns.org/cp/owl/timeinterval.owl#"},
            {"hard", "http://www.w3.org/2007/uwa/context/hardware.owl#"},
            {"vs", "http://www.w3.org/2003/06/sw-vocab-status/ns#"},
            {"usgov", "http://www.rdfabout.com/rdf/schema/usgovt/"},
            {"wdr", "http://www.w3.org/2007/05/powder#"},
            {"sail", "http://www.openrdf.org/config/sail#"},
            {"myspo", "http://purl.org/ontology/myspace#"},
            {"pmlj", "http://inference-web.org/2.0/pml-justification.owl#"},
            {"mo", "http://purl.org/ontology/mo/"},
            {"sc", "http://umbel.org/umbel/sc/"},
            {"nrl", "http://www.semanticdesktop.org/ontologies/2007/08/15/nrl#"},
            {"space", "http://purl.org/net/schemas/space/"},
            {"ptr", "http://www.w3.org/2009/pointers#"},
            {"eztag", "http://ontologies.ezweb.morfeo-project.org/eztag/ns#"},
            {"sit", "http://www.ontologydesignpatterns.org/cp/owl/situation.owl#"},
            {"lgd", "http://linkedgeodata.org/vocabulary#"},
            {"powder", "http://www.w3.org/2007/05/powder#"},
            {"nao", "http://www.semanticdesktop.org/ontologies/2007/08/15/nao#"},
            {"es", "http://eulersharp.sourceforge.net/2003/03swap/log-rules#"},
            {"omt", "http://purl.org/ontomedia/ext/common/trait#"},
            {"iswc", "http://annotation.semanticweb.org/2004/iswc#"},
            {"scovo", "http://purl.org/NET/scovo#"},
            {"swrl", "http://www.w3.org/2003/11/swrl#"},
            {"doac", "http://ramonantonio.net/doac/0.1/#"},
            {"fec", "http://www.rdfabout.com/rdf/schema/usfec/"},
            {"ac", "http://umbel.org/umbel/ac/"},
            {"ibis", "http://purl.org/ibis#"},
            {"tmo", "http://www.semanticdesktop.org/ontologies/2008/05/20/tmo#"},
            {"xhe", "http://buzzword.org.uk/rdf/xhtml-elements#"},
            {"meta", "http://www.openrdf.org/rdf/2009/metadata#"},
            {"sioct", "http://rdfs.org/sioc/types#"},
            {"user", "http://schemas.talis.com/2005/user/schema#"},
            {"mit", "http://purl.org/ontology/mo/mit#"},
            {"frbr", "http://purl.org/vocab/frbr/core#"},
            {"taxo", "http://purl.org/rss/1.0/modules/taxonomy/"},
            {"wisski", "http://wiss-ki.eu/"},
            {"wdrs", "http://www.w3.org/2007/05/powder-s#"},
            {"bibtex", "http://purl.oclc.org/NET/nknouf/ns/bibtex#"},
            {"zoology", "http://purl.org/NET/biol/zoology#"},
            {"chord", "http://purl.org/ontology/chord/"},
            {"bio2rdf", "http://bio2rdf.org/"},
            {"po", "http://purl.org/ontology/po/"},
            {"wordmap", "http://purl.org/net/ns/wordmap#"},
            {"resex", "http://resex.rkbexplorer.com/ontologies/resex#"},
            {"lt", "http://diplomski.nelakolundzija.org/LTontology.rdf#"},
            {"lingvoj", "http://www.lingvoj.org/ontology#"},
            {"osoc", "http://web-semantics.org/ns/opensocial#"},
            {"rei", "http://www.w3.org/2004/06/rei#"},
            {"ncal", "http://www.semanticdesktop.org/ontologies/2007/04/02/ncal#"},
            {"phss", "http://ns.poundhill.com/phss/1.0/"},
            {"wnschema", "http://www.cogsci.princeton.edu/~wn/schema/"},
            {"acl", "http://www.w3.org/ns/auth/acl#"},
            {"code", "http://telegraphis.net/ontology/measurement/code#"},
            {"rsa", "http://www.w3.org/ns/auth/rsa#"},
            {"cyc", "http://sw.opencyc.org/concept/"},
            {"irw", "http://www.ontologydesignpatterns.org/ont/web/irw.owl#"},
            {"spl", "http://spinrdf.org/spl#"},
            {"fresnel", "http://www.w3.org/2004/09/fresnel#"},
            {"java", "http://www.w3.org/2007/uwa/context/java.owl#"},
            {"ov", "http://open.vocab.org/terms/"},
            {"sdl", "http://purl.org/vocab/riro/sdl#"},
            {"prv", "http://purl.org/net/provenance/ns#"},
            {"prj", "http://purl.org/stuff/project/"},
            {"crypto", "http://www.w3.org/2000/10/swap/crypto#"},
            {"abc", "http://www.metadata.net/harmony/ABCSchemaV5Commented.rdf#"},
            {"p3p", "http://www.w3.org/2002/01/p3prdfv1#"},
            {"test", "http://test2.example.com/"},
            {"doc", "http://www.w3.org/2000/10/swap/pim/doc#"},
            {"tl", "http://purl.org/NET/c4dm/timeline.owl#"},
            {"wv", "http://vocab.org/waiver/terms/"},
            {"swp", "http://www.w3.org/2004/03/trix/swp-2/"},
            {"lom", "http://ltsc.ieee.org/rdf/lomv1p0/lom#"},
            {"xen", "http://buzzword.org.uk/rdf/xen#"},
            {"swh", "http://plugin.org.uk/swh-plugins/"},
            {"nmo", "http://www.semanticdesktop.org/ontologies/2007/03/22/nmo#"},
            {"acc", "http://purl.org/NET/acc#"},
            {"conserv", "http://conserv.deri.ie/ontology#"},
            {"loc", "http://www.w3.org/2007/uwa/context/location.owl#"},
            {"sv", "http://schemas.talis.com/2005/service/schema#"},
            {"umbel", "http://umbel.org/umbel#"},
            {"link", "http://www.w3.org/2006/link#"},
            {"oauth", "http://demiblog.org/vocab/oauth#"},
            {"acm", "http://www.rkbexplorer.com/ontologies/acm#"},
            {"sp", "http://spinrdf.org/sp#"},
            {"frbre", "http://purl.org/vocab/frbr/extended#"},
            {"unit", "http://data.nasa.gov/qudt/owl/unit#"},
            {"dcmitype", "http://purl.org/dc/dcmitype/"},
            {"wgs84", "http://www.w3.org/2003/01/geo/wgs84_pos#"},
            {"lastfm", "http://purl.org/ontology/last-fm/"},
            {"airport", "http://www.daml.org/2001/10/html/airport-ont#"},
            {"yago", "http://dbpedia.org/class/yago/"},
            {"am", "http://vocab.deri.ie/am#"},
            {"ddc", "http://purl.org/NET/decimalised#"},
            {"omc", "http://purl.org/ontomedia/ext/common/bestiary#"},
            {"sim", "http://purl.org/ontology/similarity/"},
            {"doclist", "http://www.junkwork.net/xml/DocumentList#"},
            {"sl", "http://www.semanlink.net/2001/00/semanlink-schema#"},
            {"urn", "http://fliqz.com/"},
            {"swandr", "http://purl.org/swan/1.2/discourse-relationships/"},
            {"resist", "http://www.rkbexplorer.com/ontologies/resist#"},
            {"hlisting", "http://sindice.com/hlisting/0.1/"},
            {"pmlr", "http://inference-web.org/2.0/pml-relation.owl#"},
            {"cycann", "http://sw.cyc.com/CycAnnotations_v1#"},
            {"cert", "http://www.w3.org/ns/auth/cert#"},
            {"qdoslf", "http://foaf.qdos.com/lastfm/schema/"},
            {"evset", "http://dsnotify.org/vocab/eventset/0.1/"},
            {"custom", "http://www.openrdf.org/config/sail/custom#"},
            {"wairole", "http://www.w3.org/2005/01/wai-rdf/GUIRoleTaxonomy#"},
            {"lode", "http://linkedevents.org/ontology/"},
            {"swanqs", "http://purl.org/swan/1.2/qualifiers/"},
            {"xl", "http://langegger.at/xlwrap/vocab#"},
            {"pimo", "http://www.semanticdesktop.org/ontologies/2007/11/01/pimo#"},
            {"pmt", "http://tipsy.googlecode.com/svn/trunk/vocab/pmt#"},
            {"web", "http://www.w3.org/2007/uwa/context/web.owl#"},
            {"dady", "http://purl.org/NET/dady#"},
            {"biol", "http://purl.org/NET/biol/ns#"},
            {"tags", "http://www.holygoat.co.uk/owl/redwood/0.1/tags/"},
            {"dcterm", "http://purl.org/dc/terms/"},
            {"politico", "http://www.rdfabout.com/rdf/schema/politico/"},
            {"ire", "http://www.ontologydesignpatterns.org/cpont/ire.owl#"},
            {"ecs", "http://rdf.ecs.soton.ac.uk/ontology/ecs#"},
            {"sesame", "http://www.openrdf.org/schema/sesame#"},
            {"gpt", "http://purl.org/vocab/riro/gpt#"},
            {"swanpav", "http://purl.org/swan/1.2/pav/"},
            {"c4n", "http://vocab.deri.ie/c4n#"},
            {"sede", "http://eventography.org/sede/0.1/"},
            {"profiling", "http://ontologi.es/profiling#"},
            {"grddl", "http://www.w3.org/2003/g/data-view#"},
            {"cnt", "http://www.w3.org/2008/content#"},
            {"contact", "http://www.w3.org/2000/10/swap/pim/contact#"},
            {"like", "http://ontologi.es/like#"},
            {"vote", "http://www.rdfabout.com/rdf/schema/vote/"},
            {"uri", "http://purl.org/NET/uri#"},
            {"swanag", "http://purl.org/swan/1.2/agents/"},
            {"omp", "http://purl.org/ontomedia/ext/common/profession#"},
            {"ping", "http://purl.org/net/pingback/"},
            {"states", "http://www.w3.org/2005/07/aaa#"},
            {"list", "http://www.w3.org/2000/10/swap/list#"},
            {"atom", "http://www.w3.org/2005/Atom/"},
            {"puc", "http://purl.org/NET/puc#"},
            {"formats", "http://www.w3.org/ns/formats/"},
            {"common", "http://www.w3.org/2007/uwa/context/common.owl#"},
            {"lifecycle", "http://purl.org/vocab/lifecycle/schema#"},
            {"protege", "http://protege.stanford.edu/system#"},
            {"og", "http://opengraphprotocol.org/schema/"},
            {"pr", "http://ontologi.es/profiling#"},
            {"bsbm", "http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/"},
            {"climb", "http://climb.dataincubator.org/vocabs/climb/"},
            {"admin", "http://webns.net/mvcb/"},
            {"osgb", "http://data.ordnancesurvey.co.uk/id/"},
            {"fed", "http://www.openrdf.org/config/sail/federation#"},
            {"audio", "http://purl.org/media/audio#"},
            {"skosxl", "http://www.w3.org/2008/05/skos-xl#"},
            {"ddl", "http://purl.org/vocab/riro/ddl#"},
            {"sm", "http://topbraid.org/sparqlmotion#"},
            {"scv", "http://purl.org/NET/scovo#"},
            {"scot", "http://scot-project.org/scot/ns#"},
            {"lfm", "http://purl.org/ontology/last-fm/"},
            {"exif", "http://www.w3.org/2003/12/exif/ns#"},
            {"omm", "http://purl.org/ontomedia/core/media#"},
            {"swanci", "http://purl.org/swan/1.2/citations/"},
            {"rdfa", "http://www.w3.org/ns/rdfa#"},
            {"daml", "http://www.daml.org/2001/03/daml+oil#"},
            {"affy", "http://www.affymetrix.com/community/publications/affymetrix/tmsplice#"},
            {"string", "http://www.w3.org/2000/10/swap/string#"},
            {"push", "http://www.w3.org/2007/uwa/context/push.owl#"},
            {"smiley", "http://www.smileyontology.com/ns#"},
            {"commerce", "http://purl.org/commerce#"},
            {"gob", "http://purl.org/ontology/last-fm/"},
            {"courseware", "http://courseware.rkbexplorer.com/ontologies/courseware#"},
            {"nco", "http://www.semanticdesktop.org/ontologies/2007/03/22/nco#"},
            {"xesam", "http://freedesktop.org/standards/xesam/1.0/core#"},
            {"psych", "http://purl.org/vocab/psychometric-profile/"},
            {"obj", "http://www.openrdf.org/rdf/2009/object#"},
            {"plink", "http://buzzword.org.uk/rdf/personal-link-types#"},
            {"lfn", "http://www.dotnetrdf.org/leviathan#"},
            {"opm", "http://openprovenance.org/ontology#"},
            {"sml", "http://topbraid.org/sparqlmotionlib#"},
            {"moat", "http://moat-project.org/ns#"},
            {"ldap", "http://purl.org/net/ldap/"},
            {"dummy", "http://hello.com/"},
            {"music", "http://musicontology.com/"},
            {"smf", "http://topbraid.org/sparqlmotionfunctions#"},
            {"product", "http://purl.org/commerce/product#"},
            {"nid3", "http://www.semanticdesktop.org/ontologies/2007/05/10/nid3#"},
            {"soft", "http://www.w3.org/2007/uwa/context/software.owl#"},
            {"net", "http://www.w3.org/2007/uwa/context/network.owl#"},
            {"so", "http://purl.org/ontology/symbolic-music/"},
            {"xhtmlvocab", "http://www.w3.org/1999/xhtml/vocab/"},
            {"nexif", "http://www.semanticdesktop.org/ontologies/2007/05/10/nexif#"},
            {"play", "http://uriplay.org/spec/ontology/#"},
            {"mysql", "http://web-semantics.org/ns/mysql/"},
            {"resource", "http://purl.org/vocab/resourcelist/schema#"},
            {"trackback", "http://madskills.com/public/xml/rss/module/trackback/"},
            {"dbpp", "http://dbpedia.org/property/"},
            {"gold", "http://purl.org/linguistics/gold/"},
            {"movie", "http://data.linkedmdb.org/resource/movie/"},
            {"film", "http://data.linkedmdb.org/resource/film/"},
            {"xforms", "http://www.w3.org/2002/xforms/"},
            {"sysont", "http://ns.ontowiki.net/SysOnt/"},
            {"label", "http://purl.org/net/vocab/2004/03/label#"},
            {"okkam", "http://models.okkam.org/ENS-core-vocabulary#"},
            {"dbpediaowl", "http://dbpedia.org/ontology/"},
            {"swanco", "http://purl.org/swan/1.2/swan-commons/"},
            {"dailymed", "http://www4.wiwiss.fu-berlin.de/dailymed/resource/dailymed/"},
            {"meetup", "http://www.lotico.com/meetup/"},
            {"status", "http://ontologi.es/status#"},
            {"timeline", "http://purl.org/NET/c4dm/timeline.owl#"},
            {"oat", "http://openlinksw.com/schemas/oat/"},
            {"lotico", "http://www.lotico.com/meetup/"},
            {"dcat", "http://vocab.deri.ie/dcat#"},
            {"wgs", "http://www.w3.org/2003/01/geo/wgs84_pos#"},
            {"imreg", "http://www.w3.org/2004/02/image-regions#"},
            {"wgspos", "http://www.w3.org/2003/01/geo/wgs84_pos#"},
            {"opensearch", "http://a9.com/-/spec/opensearch/1.1/"},
            {"ezcontext", "http://ontologies.ezweb.morfeo-project.org/ezcontext/ns#"},
            {"coin", "http://purl.org/court/def/2009/coin#"},
            {"time", "http://www.w3.org/2006/time#"},
            {"bib", "http://zeitkunst.org/bibtex/0.1/bibtex.owl#"},
            {"rooms", "http://vocab.deri.ie/rooms#"},
            {"sparql", "http://www.openrdf.org/config/repository/sparql#"},
            {"opo", "http://online-presence.net/opo/ns#"},
            {"tripfs", "http://purl.org/tripfs/2010/02#"},
            {"pmlp", "http://inference-web.org/2.0/pml-provenance.owl#"},
            {"ttl", "http://www.w3.org/2008/turtle#"},
            {"pmlt", "http://inference-web.org/2.0/pml-trust.owl#"},
            {"drugbank", "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/"},
            {"drugbankvocab", "http://www4.wiwiss.fu-berlin.de/drugbank/vocab/resource/class/"},
            {"geographis", "http://telegraphis.net/ontology/geography/geography#"},
            {"sioca", "http://rdfs.org/sioc/actions#"},
            {"wn20schema", "http://www.w3.org/2006/03/wn/wn20/schema/"},
            {"ufmedia", "http://purl.org/microformat/hmedia/"},
            {"mf", "http://poshrdf.org/ns/mf#"},
            {"txn", "http://lod.taxonconcept.org/ontology/txn.owl#"},
            {"meteo", "http://purl.org/ns/meteo#"},
            {"lib", "http://schemas.talis.com/2005/library/schema#"},
            {"rec", "http://purl.org/ontology/rec/core#"},
            {"awol", "http://bblfish.net/work/atom-owl/2006-06-06/#"},
            {"aifb", "http://www.aifb.kit.edu/id/"},
            {"sawsdl", "http://www.w3.org/ns/sawsdl#"},
            {"copyright", "http://rhizomik.net/ontologies/2008/05/copyrightonto.owl#"},
            {"pdo", "http://ontologies.smile.deri.ie/pdo#"},
            {"evopat", "http://ns.aksw.org/Evolution/"},
            {"whois", "http://www.kanzaki.com/ns/whois#"},
            {"rulz", "http://purl.org/NET/rulz#"},
            {"isi", "http://purl.org/ontology/is/inst/"},
            {"qb", "http://purl.org/linked-data/cube#"},
            {"xbrli", "http://www.xbrl.org/2003/instance#"},
            {"ya", "http://blogs.yandex.ru/schema/foaf/"},
            {"ref", "http://purl.org/vocab/relationship/"},
            {"ean", "http://openean.kaufkauf.net/id/"},
            {"wlp", "http://weblab-project.org/core/model/property/processing/"},
            {"remus", "http://www.semanticweb.org/ontologies/2010/6/Ontology1279614123500.owl#"},
            {"sdmx", "http://purl.org/linked-data/sdmx#"},
            {"derecho", "http://purl.org/derecho#"},
            {"ist", "http://purl.org/ontology/is/types/"},
            {"cos", "http://www.inria.fr/acacia/corese#"},
            {"dayta", "http://dayta.me/resource#"},
            {"yoda", "http://purl.org/NET/yoda#"},
            {"swivt", "http://semantic-mediawiki.org/swivt/1.0#"},
            {"act", "http://www.w3.org/2007/rif-builtin-action#"},
            {"dnr", "http://www.dotnetrdf.org/configuration#"},
            {"game", "http://data.totl.net/game/"},
            {"tarot", "http://data.totl.net/tarot/card/"},
            {"hcterms", "http://purl.org/uF/hCard/terms/"},
            {"loticoowl", "http://www.lotico.com/ontology/"},
            {"uniprot", "http://purl.uniprot.org/core/"},
            {"olo", "http://purl.org/ontology/olo/core#"},
            {"posh", "http://poshrdf.org/ns/posh/"},
            {"isq", "http://purl.org/ontology/is/quality/"},
            {"tripfs2", "http://purl.org/tripfs/2010/06#"},
            {"oc", "http://opencoinage.org/rdf/"},
            {"gv", "http://rdf.data-vocabulary.org/#"},
            {"lark1", "http://users.utcluj.ro/~raluca/ontology/Ontology1279614123500.owl#"},
            {"is", "http://purl.org/ontology/is/core#"},
            {"anca", "http://www.semanticweb.org/ontologies/2010/6/Ontology1279614123500.owl#"},
            {"nt", "http://ns.inria.fr/nicetag/2010/09/09/voc#"},
            {"conv", "http://purl.org/twc/vocab/conversion/"},
            {"search", "http://sindice.com/vocab/search#"},
            {"geospecies", "http://rdf.geospecies.org/ont/geospecies#"},
            {"geoes", "http://geo.linkeddata.es/page/ontology/"},
            {"wo", "http://purl.org/ontology/wo/core#"},
            {"dgfoaf", "http://west.uni-koblenz.de/ontologies/2010/07/dgfoaf.owl#"},
            {"opmv", "http://purl.org/net/opmv/ns#"},
            {"opus", "http://lsdis.cs.uga.edu/projects/semdis/opus#"},
            {"ao", "http://purl.org/ontology/ao/core#"},
            {"prot", "http://www.proteinontology.info/po.owl#"},
            {"r2r", "http://www4.wiwiss.fu-berlin.de/bizer/r2r/"},
            {"protons", "http://proton.semanticweb.org/2005/04/protons#"},
            {"cco", "http://purl.org/ontology/cco/core#"},
            {"w3p", "http://prov4j.org/w3p/"},
            {"gridworks", "http://purl.org/net/opmv/types/gridworks#"},
            {"toby", "http://tobyinkster.co.uk/#"},
            {"ma", "http://www.w3.org/ns/ma-ont#"},
            {"httph", "http://www.w3.org/2007/ont/httph#"},
            {"ct", "http://data.linkedct.org/resource/linkedct/"},
            {"lp", "http://launchpad.net/rdf/launchpad#"},
            {"session", "http://redfoot.net/2005/session#"},
            {"oo", "http://purl.org/openorg/"},
            {"openlinks", "http://www.openlinksw.com/schemas/virtrdf#"},
            {"eu", "http://eulersharp.sourceforge.net/2003/03swap/log-rules#"},
            {"nsa", "http://multimedialab.elis.ugent.be/organon/ontologies/ninsuna#"},
            {"rr", "http://www.w3.org/ns/r2rml#"},
            {"dv", "http://rdf.data-vocabulary.org/#"},
            {"linkedct", "http://data.linkedct.org/resource/linkedct/"},
            {"compass", "http://purl.org/net/compass#"},
            //{"#obs","???"},
            {"cito", "http://purl.org/net/cito/"},
            {"postcode", "http://data.ordnancesurvey.co.uk/id/postcodeunit/"},
            {"rail", "http://ontologi.es/rail/vocab#"},
            {"com", "http://purl.org/commerce#"},
            {"dul", "http://www.loa-cnr.it/ontologies/DUL.owl#"},
            {"tio", "http://purl.org/tio/ns#"},
            {"crm", "http://purl.org/NET/cidoc-crm/core#"},
            {"dblp", "http://www4.wiwiss.fu-berlin.de/dblp/terms.rdf#"},
            {"odp", "http://ontologydesignpatterns.org/"},
            {"sindice", "http://vocab.sindice.net/"},
            {"pobo", "http://purl.obolibrary.org/obo/"},
            {"c4o", "http://purl.org/spar/c4o/"},
            {"countries", "http://eulersharp.sourceforge.net/2003/03swap/countries#"},
            {"drug", "http://www.agfa.com/w3c/2009/drugTherapy#"},
            {"lvont", "http://lexvo.org/ontology#"},
            {"events", "http://eulersharp.sourceforge.net/2003/03swap/event#"},
            {"umbelrc", "http://umbel.org/umbel/rc/"},
            {"xtypes", "http://purl.org/xtypes/"},
            {"enc", "http://www.w3.org/2001/04/xmlenc#"},
            {"opwn", "http://www.ontologyportal.org/WordNet.owl#"},
            {"freebase", "http://rdf.freebase.com/ns/"},
            {"human", "http://eulersharp.sourceforge.net/2003/03swap/human#"},
            {"humanbody", "http://eulersharp.sourceforge.net/2003/03swap/humanBody#"},
            {"languages", "http://eulersharp.sourceforge.net/2003/03swap/languages#"},
            {"organism", "http://eulersharp.sourceforge.net/2003/03swap/organism#"},
            {"units", "http://eulersharp.sourceforge.net/2003/03swap/unitsExtension#"},
            {"bioskos", "http://eulersharp.sourceforge.net/2003/03swap/bioSKOSSchemes#"},
            {"transmed", "http://www.w3.org/2001/sw/hcls/ns/transmed/"},
            {"conversion", "http://purl.org/twc/vocab/conversion/"},
            {"genab", "http://eulersharp.sourceforge.net/2003/03swap/genomeAbnormality#"},
            {"organiz", "http://eulersharp.sourceforge.net/2003/03swap/organization#"},
            {"agents", "http://eulersharp.sourceforge.net/2003/03swap/agent#"},
            {"nndsr", "http://semanticdiet.com/schema/usda/nndsr/"},
            {"agent", "http://eulersharp.sourceforge.net/2003/03swap/agent#"},
            //	{"#porn","???"},
            {"elog", "http://eulersharp.sourceforge.net/2003/03swap/log-rules#"},
            {"malignneo", "http://www.agfa.com/w3c/2009/malignantNeoplasm#"},
            {"clineva", "http://www.agfa.com/w3c/2009/clinicalEvaluation#"},
            {"hemogram", "http://www.agfa.com/w3c/2009/hemogram#"},
            {"sig", "http://purl.org/signature#"},
            {"spacerel", "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/"},
            {"care", "http://eulersharp.sourceforge.net/2003/03swap/care#"},
            {"hartigprov", "http://purl.org/net/provenance/ns#"},
            {"cpm", "http://catalogus-professorum.org/cpm/"},
            {"ps", "http://purl.org/payswarm#"},
            {"environ", "http://eulersharp.sourceforge.net/2003/03swap/environment#"},
            {"healthcare", "http://www.agfa.com/w3c/2009/healthCare#"},
            {"ass", "http://uptheasset.org/ontology#"},
            {"infection", "http://www.agfa.com/w3c/2009/infectiousDisorder#"},
            {"ccard", "http://purl.org/commerce/creditcard#"},
            {"uni", "http://purl.org/weso/uni/uni.html#"},
            {"geodata", "http://sws.geonames.org/"},
            {"req", "http://ns.softwiki.de/req/"},
            {"clinproc", "http://www.agfa.com/w3c/2009/clinicalProcedure#"},
            {"hospital", "http://www.agfa.com/w3c/2009/hospital#"},
            {"quantities", "http://eulersharp.sourceforge.net/2003/03swap/quantitiesExtension#"},
            {"pgterms", "http://www.gutenberg.org/2009/pgterms/"},
            {"quak", "http://dev.w3.org/cvsweb/2000/quacken/vocab#"},
            {"disease", "http://www.agfa.com/w3c/2009/humanDisorder#"},
            {"arch", "http://purl.org/archival/vocab/arch#"},
            {"voaf", "http://mondeca.com/foaf/voaf#"},
            {"pbo", "http://purl.org/ontology/pbo/core#"},
            {"span", "http://www.ifomis.org/bfo/1.1/span#"},
            {"uta", "http://uptheasset.org/ontology#"},
            {"esd", "http://def.esd.org.uk/"},
            {"sider", "http://www4.wiwiss.fu-berlin.de/sider/resource/sider/"},
            {"diseasome", "http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/"},
            {"bibbaseontology", "http://data.bibbase.org/ontology/#"}};
    
    /**
     * Adds a new prefix to the PrefixHelper (not persistent). Threadsafe.
     *
     * @param prefix
     *         the abbreviated url, example "dbo"
     * @param uri
     *         the url to abbreviate, e.g. "http://dbpedia.org/ontology/
     */
    public static void addPrefix(String prefix, String uri) {
        synchronized (LazyLoaded.prefixToURI) {
            synchronized (LazyLoaded.uriToPrefix) {
                LazyLoaded.prefixToURI.put(prefix, uri);
                LazyLoaded.uriToPrefix.put(uri, prefix);
            }
        }
    }

    /**
     * Generated an unused prefix for a uri if there is none and add it to the PrefixHelper (not persistent).
     *
     * @param fullURI
     *         the full uri, for example "http://dbpedia.org/resource/Elephant"
     * @return String[]{prefix,url}
     */
    public static String[] generatePrefix(String fullURI) {
        String base = getBase(fullURI);
        if (getPrefix(base) != null) {
            return new String[]{getPrefix(base), base};
        } // already existing
        // try to generate something meaningful
        String prefix = base.replace("http://", "").split("[\\./#]")[0];
        if (prefix.isEmpty() || getPrefixes().containsKey(prefix)) {
            // didn't work, just generate something
            prefix = "prefix" + 0;//base.hashCode();
        }
        if (prefix.isEmpty() || getPrefixes().containsKey(prefix)) {
            // this loop should almost never ever be entered
            int count = 0;
            do {
                prefix = "prefix" + count;
                count++;
            }
            while (prefix.isEmpty() || getPrefixes().containsKey(prefix));
        }
        addPrefix(prefix, base);
        return new String[]{prefix, base};
    }

    public static String addPrefixes(String query) {
        return formatPrefixes(restrictPrefixes(getPrefixes(), query)) + query;
    }

    /**
     * 
     * returns only the subset of the given prefixes that is referenced in the query
     * @param prefixes prefixes to restrict
     * @param query query from which the prefixes to be restricted are extracted
     * @return restricted prefixes
     */
    public static Map<String, String> restrictPrefixes(Map<String, String> prefixes, String query) {
        Map<String, String> restrictedPrefixes = new HashMap<String, String>();
        for (String prefix : prefixes.keySet()) {
            if (query.contains(prefix + ':')) {
                restrictedPrefixes.put(prefix, prefixes.get(prefix));
            }
        }
        return restrictedPrefixes;
    }

    /**
     * returns prefixes
     * @return prefixes
     */
    public static Map<String, String> getPrefixes() {
        return Collections.unmodifiableMap(prefixToURI);
    }

    /**
     * returns prefixes
     * @param uri uri
     * @return prefix prefix
     */
    public static String getPrefix(String uri) {
        return uriToPrefix.get(uri);
    }

    /**
     * returns uri
     * @param prefix prefix
     * @return uri uri
     */
    public static synchronized String getURI(String prefix) {
//		if(prefix.endsWith(":"))
//			prefix = prefix.substring(0, prefix.length()-1);
        return prefixToURI.get(prefix);
    }

    /**
     * @param uri
     *         a URI, either in expanded form like "http://dbpedia.org/ontology/Settlement" or in abbreviated form like "dbo:Settlement".
     * @return the suffix (last part) of the URI. The suffix of "http://dbpedia.org/ontology/Settlement" is "Settlement", for example.
     */
    public static String getSuffix(String uri) {
        int baseURIEnd = Math.max(Math.max(uri.lastIndexOf('#'), uri.lastIndexOf('/')), uri.lastIndexOf(':'));
        if (baseURIEnd == -1) {
            return uri;
        }
        return uri.substring(baseURIEnd + 1);
    }

    /**
     * @param uri
     *         a URI, either in expanded form like "http://dbpedia.org/ontology/Settlement" or in abbreviated form like "dbo:Settlement".
     * @return the base (first part) of the URI. The base of "http://dbpedia.org/ontology/Settlement" is "http://dbpedia.org/ontology/", for example.
     */
    public static String getBase(String uri) {
        int baseURIEnd = Math.max(Math.max(uri.lastIndexOf('#'), uri.lastIndexOf('/')), uri.lastIndexOf(':'));
        if (baseURIEnd == -1) {
            return uri;
        }
        return uri.substring(0, baseURIEnd + 1);
    }

    /**
     * returns the abbreviation of an uri
     * @param uri uri
     * @return abbreviation abbreviation
     */
    public static String abbreviate(String uri) {
        if (!uri.startsWith("http://")) return uri;
        int baseURIEnd = Math.max(uri.lastIndexOf('#'), uri.lastIndexOf('/'));
        if (baseURIEnd == -1) {
            return uri;
        }
        String baseURI = uri.substring(0, baseURIEnd + 1);
        String prefix = getPrefix(baseURI);
        if (prefix != null) {
            return uri.replace(baseURI, prefix + ':');
        }
        return uri;
    }

    /**
     * expands an uri (reversal of abbreviation)
     * @param shortURI shortURI
     * @return expanded URI
     */
    public static String expand(String shortURI) {
        // already expanded?
        if (shortURI.startsWith("http://")) {
            return shortURI;
        }
        int prefixEnd = shortURI.indexOf(':');
        // no prefix found?
        if (prefixEnd == -1) {
            return shortURI;
        }
        String prefix = shortURI.substring(0, prefixEnd);
        String baseURI = getURI(prefix);
        if (baseURI == null) {
            return shortURI;
        }

        return shortURI.replace(prefix + ':', baseURI);
    }


    /**
     * Transforms a prefix map into sparql "PREFIX x: {@literal <}...{@literal >}" statements.
     * @param prefixes prefixes
     * @return formatted prefix
     */
    public static String formatPrefixes(Map<String, String> prefixes) {
        if (prefixes.isEmpty()) return "";
        StringBuffer prefixSPARQLString = new StringBuffer();
        for (String key : prefixes.keySet()) {
            prefixSPARQLString.append("PREFIX " + key + ": <" + prefixes.get(key) + ">" + '\n');
        }
        return prefixSPARQLString.substring(0, prefixSPARQLString.length() - 1);
    }

    protected static class LazyLoaded {
        static final Map<String, String> prefixToURI;
        static final Map<String, String> uriToPrefix;

        static {
            prefixToURI = new HashMap<String, String>();
            uriToPrefix = new HashMap<String, String>();

            for (String[] prefix : prefixArray) {
                prefixToURI.put(prefix[0], prefix[1]);
                // file is sorted by popularity of the prefix in descending order
                // in case of conflicts we want the most popular prefix
                if (!uriToPrefix.containsKey(prefix[1])) {
                    uriToPrefix.put(prefix[1], prefix[0]);
                }
            }
        }
    }
    
}
