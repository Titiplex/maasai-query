package com.aixuniversity.maal;

import ai.djl.Device;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.AbstractBlock;
import ai.djl.nn.Parameter;
import ai.djl.training.ParameterStore;
import ai.djl.util.PairList;

public class LoRALinear extends AbstractBlock {

    private static final byte VERSION = 1;
    private final int inF, outF, rank;
    private final float alpha;
    private final Parameter weight, loraA, loraB;

    public LoRALinear(int inF, int outF, int rank, float alpha) {
        super(VERSION);
        this.inF = inF;
        this.outF = outF;
        this.rank = rank;
        this.alpha = alpha;
        weight = addParameter(Parameter.builder().setName("weight").setType(Parameter.Type.WEIGHT).build());
        loraA = addParameter(Parameter.builder().setName("loraA").setType(Parameter.Type.WEIGHT).optRequiresGrad(true).build());
        loraB = addParameter(Parameter.builder().setName("loraB").setType(Parameter.Type.WEIGHT).optRequiresGrad(true).build());
    }

    @Override
    protected NDList forwardInternal(ParameterStore ps, NDList in, boolean training, PairList<String, Object> params) {
        NDArray x = in.singletonOrThrow();
        Device d = x.getDevice();
        NDArray W = ps.getValue(weight, d, training);
        NDArray A = ps.getValue(loraA, d, training);
        NDArray B = ps.getValue(loraB, d, training);

        NDArray base = x.dot(W.transpose());
        NDArray lora = x.dot(B.transpose()).dot(A.transpose()).mul(alpha / rank);
        return new NDList(base.add(lora));
    }

    @Override
    public Shape[] getOutputShapes(Shape[] ish) {
        return new Shape[]{new Shape(ish[0].get(0), outF)};
    }

    @Override
    public void initializeChildBlocks(NDManager m, DataType dt, Shape... ish) {
        weight.setArray(m.randomNormal(0, 0.02f, new Shape(outF, inF), dt));
        loraA.setArray(m.randomNormal(0, 0.02f, new Shape(rank, outF), dt));
        loraB.setArray(m.randomNormal(0, 0.02f, new Shape(inF, rank), dt));
    }
}
