package com.mit.AI;

public enum AIObstructionTypes {
  SOLID(1),
  AIR(0),
  BREAKING(2);

  public int val;

  AIObstructionTypes(int val) {
    this.val = val;
  }
}
