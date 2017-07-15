package hex.tree;

import water.DKV;
import water.Iced;
import water.Key;

public class CompressedForest extends Iced<CompressedTree> {

  public Key<CompressedTree>[/*_ntrees*/][/*_nclass*/] _treeKeys;
  public String[][] _domains;

  public CompressedForest(Key<CompressedTree>[][] treeKeys, String[][] domains) {
    _treeKeys = treeKeys;
    _domains = domains;
  }

  public LocalCompressedForest fetch() {
    int ntrees = _treeKeys.length;
    CompressedTree[][] trees = new CompressedTree[ntrees][];
    for (int t = 0; t < ntrees; t++) {
      Key[] treek = _treeKeys[t];
      trees[t] = new CompressedTree[treek.length];
      // FIXME remove get by introducing fetch class for all trees
      for (int i = 0; i < treek.length; i++)
        if (treek[i] != null)
          trees[t][i] = DKV.get(treek[i]).get();
    }
    return new LocalCompressedForest(trees, _domains);
  }

  public static class LocalCompressedForest {
    public CompressedTree[][] _trees;
    public String[][] _domains;

    private LocalCompressedForest(CompressedTree[][] trees, String[][] domains) {
      _trees = trees;
      _domains = domains;
    }

    /** Score given tree on the row of data.
     *  @param data row of data
     *  @param preds array to hold resulting prediction
     *  @param tidx index of a tree (leads to representation of a single regression tree, or multi tree)  */
    public void scoreTree(double data[], double preds[], int tidx) {
      CompressedTree[] ts = _trees[tidx];
      for( int c=0; c<ts.length; c++ )
        if( ts[c] != null )
          preds[ts.length==1?0:c+1] += ts[c].score(data, _domains);
    }
  }

}
