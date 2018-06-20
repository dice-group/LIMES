

s = open("/home/swante/downloads/go.obo").read()

terms = [it for it in s.split("\n\n") if it.startswith("[Term]")]
rdf_pairs = []

print(len(terms))
print(terms[0])
print(terms[-1])

predicate = "is_connected_with"

for term in terms:
	source = None
	for line in term.split("\n"):
		if not ("GO:" in line):
			continue
		parts = [it for it in line.split(" ") if it.startswith("GO:")]
		if len(parts) == 0:
			continue
		go_id = parts[0]
		if source is None:
			source = go_id
		else:
			target = go_id
			rdf_pairs.append((source, target))

out_file = open("/home/swante/downloads/output.rdf", "w")
for (source, target) in rdf_pairs:
	output_line = "<http://purl.obolibrary.org/obo/" + source + "> <is_connected_with> " + "<http://purl.obolibrary.org/obo/"+target + ">\n"
	out_file.write(output_line)
	out_file.flush()
out_file.close()