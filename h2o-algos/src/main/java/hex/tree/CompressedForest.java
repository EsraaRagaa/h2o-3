package hex.tree;

import water.DKV;
import water.Iced;
import water.Key;

public class CompressedForest extends Iced<CompressedTree> {

  public Key<CompressedTree>[/*_ntrees*/][/*_nclass*/] _treeKeys;

  public CompressedForest(Key<CompressedTree>[][] treeKeys) { _treeKeys = treeKeys; }

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
    return new LocalCompressedForest(trees);
  }

  public static class LocalCompressedForest {
    public CompressedTree[][] _trees;

    LocalCompressedForest(CompressedTree[][] trees) { _trees = trees; }

    /** Score given tree on the row of data.
     *  @param data row of data
     *  @param preds array to hold resulting prediction
     *  @param tidx index of a tree (leads to representation of a single regression tree, or multi tree)  */
    public void scoreTree(double data[], double preds[], int tidx) {
      CompressedTree[] ts = _trees[tidx];
      for( int c=0; c<ts.length; c++ )
        if( ts[c] != null )
          preds[ts.length==1?0:c+1] += ts[c].score(data);
    }
  }

}
