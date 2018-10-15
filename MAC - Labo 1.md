# MAC - Labo 1

[TOC]

## D - Understanding the Lucene API

### 1 - Does the command line demo use stopword removal ? Explain how you find out the answer.

Yes the command line demo uses stopword removal. We tested it simply by making the following requests:

- `the` : no results, mean empty request performed

- `kill` : 1 result
- `kill the `: 1 result (same)
- `the kill `: 1 result (same)

### 2 - Does the command line demo use stemming ? Explain how you find out the answer.

No, it doesn't use stemming, we checked it easily by performing the following two queries:
- `kill` : 1 result
- `killed `: 0 results

###3 - Is the search of the command line demo case insensitive? How did you find out the answer?

Yes it is case insensitive, we checked it by performing the following queries:

- `kill`
- `KILL`
- `KiLl`

which all returned the same single result

###4 - Does it matter whether stemming occurs before or after stopword removal? Consider this as a general question.

Well it depends if our list of stopwords includes stems or not. If so we should first apply stemming and then stop words removal (to stem those stop words that might not be removed). 

Otherwise we should perform stop words removal to gain performance (avoid stemming stop words that are eventually removed)What should be added to the code to have access to the term vector in the index ?

##F - Indexing and Searching the CACM collection

###Indexing

####4 - Keep the publication id in the index and show it in the results set of your queries.

```
TODO
```

####5 - explain which field type can be used for id, title, summary and author.

- `id` : `LongPoint` and `StoredField`

- `title` : `Field` with `TextField.TYPE_STORED`

- `summary ` : 

  ```java
  FieldType sumType = new FieldType(TextField.TYPE_STORED); //to be stored
  sumType.setIndexOptions(IndexOptions.DOCS); // TODO Hochet
  sumType.storeTermVectors(); //  to the code to have access to the term vector in the index
  ```

- `author` : `StringField` with `TextField.TYPE_STORED`

#### 6 - What should be added to the code to have access to the term vector in the index? 

https://lucene.apache.org/core/5_2_1/core/org/apache/lucene/index/IndexableFieldType.html#storeTermVectors

```java
storeTermVectors();
```

#### Code of indexing

```java
@Override
    public void onNewDocument(Long id, String authors, String title, String summary) {

        Document doc = new Document();

        doc.add(new LongPoint("id", id));
        doc.add(new StoredField("id", id));

        if (authors != null && authors.length() > 0)
            for (String author : authors.split(";"))
                    doc.add(new StringField("authors", author, Field.Store.YES));

        doc.add(new Field("title", title, TextField.TYPE_STORED));

        if (summary != null && summary.length() > 0) {

            FieldType sumType = new FieldType(TextField.TYPE_STORED);
            sumType.setIndexOptions(IndexOptions.DOCS);
            sumType.storeTermVectors();
            doc.add(new Field("summary", summary, sumType));
        }
        try {
            this.indexWriter.addDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

###Using different Analyzers

### Reading Index

####1- Author with maximum of publications

Thacher Jr., H. C. with 38 publications

####2 - Top 10 terms in the title field

| Top ranking terms | Frequency in the title field |
| ----------------- | ---------------------------- |
| algorithm         | 961                          |
| computer          | 260                          |
| system            | 172                          |
| programming       | 154                          |
| method            | 124                          |
| data              | 110                          |
| systems           | 108                          |
| language          | 99                           |
| program           | 93                           |
| matrix            | 82                           |

####Code

```java
Comparator comparator = new HighFreqTerms.DocFreqComparator();

try {
	TermStats[] stats = HighFreqTerms.getHighFreqTerms(this.indexReader, numTerms, field, comparator);
	System.out.println("Top ranking terms for field [" + field + "] are: ");
	for(TermStats stat : stats)
		System.out.println(stat.termtext.utf8ToString() + " with " + stat.docFreq + " frequencies");
} catch (Exception e) {
	System.out.println("Failed querying");
}
```

