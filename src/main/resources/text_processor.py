import scispacy
import spacy
import sys
import warnings

def process_text():
    text = sys.stdin.read()
    # print(text)
    text = """Myeloid derived suppressor cells (MDSC) are immature 
    myeloid cells with immunosuppressive activity. 
    They accumulate in tumor-bearing mice and humans 
    with different types of cancer, including hepatocellular 
    carcinoma (HCC). Crocin is a good medicine."""
    # Load scispacy biomedical model
    nlp = spacy.load("/home/gua00va/en_ner_bc5cdr_md-0.5.3/en_ner_bc5cdr_md/en_ner_bc5cdr_md-0.5.3")

    doc = nlp(text)
    biomedical_entities = [(ent.text, ent.label_) for ent in doc.ents if ent.label_ in ['DISEASE', 'CHEMICAL']]

    for entity in biomedical_entities:
        print(f"{entity[0]}")

if __name__ == "__main__":
    warnings.simplefilter(action='ignore', category=FutureWarning)
    process_text()


