import java.util.*;

public class Algo {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("{\"error\": \"Invalid arguments\"}");
            return;
        }

        int algoType = Integer.parseInt(args[0]);
        int numFrames = Integer.parseInt(args[1]);
        int numPages = Integer.parseInt(args[2]);

        if (args.length < 3 + numPages) {
            System.out.println("{\"error\": \"Not enough arguments for reference string\"}");
            return;
        }

        int[] pages = new int[numPages];
        for (int i = 0; i < numPages; i++) {
            pages[i] = Integer.parseInt(args[3 + i]);
        }

        List<Step> steps = new ArrayList<>();
        int faults = 0;

        if (algoType == 1) { // FIFO
            int[] frames = new int[numFrames];
            Arrays.fill(frames, -1);
            int pointer = 0;

            for (int p = 0; p < numPages; p++) {
                int page = pages[p];
                boolean hit = false;
                for (int i = 0; i < numFrames; i++) {
                    if (frames[i] == page) {
                        hit = true;
                        break;
                    }
                }
                if (!hit) {
                    frames[pointer] = page;
                    pointer = (pointer + 1) % numFrames;
                    faults++;
                }
                steps.add(new Step(page, hit, frames.clone()));
            }
        } 
        else if (algoType == 2) { // LRU
            int[] frames = new int[numFrames];
            Arrays.fill(frames, -1);
            int[] lastUsed = new int[numFrames];

            for (int p = 0; p < numPages; p++) {
                int page = pages[p];
                boolean hit = false;
                for (int i = 0; i < numFrames; i++) {
                    if (frames[i] == page) {
                        hit = true;
                        lastUsed[i] = p;
                        break;
                    }
                }
                if (!hit) {
                    int replaceIdx = -1;
                    for (int i = 0; i < numFrames; i++) {
                        if (frames[i] == -1) {
                            replaceIdx = i;
                            break;
                        }
                    }
                    if (replaceIdx == -1) { // Need to find LRU
                        int minIdx = 0;
                        for (int i = 1; i < numFrames; i++) {
                            if (lastUsed[i] < lastUsed[minIdx]) {
                                minIdx = i;
                            }
                        }
                        replaceIdx = minIdx;
                    }
                    frames[replaceIdx] = page;
                    lastUsed[replaceIdx] = p;
                    faults++;
                }
                steps.add(new Step(page, hit, frames.clone()));
            }
        }
        else if (algoType == 3) { // Optimal
            int[] frames = new int[numFrames];
            Arrays.fill(frames, -1);

            for (int p = 0; p < numPages; p++) {
                int page = pages[p];
                boolean hit = false;
                for (int i = 0; i < numFrames; i++) {
                    if (frames[i] == page) {
                        hit = true;
                        break;
                    }
                }
                if (!hit) {
                    int replaceIdx = -1;
                    for (int i = 0; i < numFrames; i++) {
                        if (frames[i] == -1) {
                            replaceIdx = i;
                            break;
                        }
                    }
                    if (replaceIdx == -1) { // Need to find Optimal
                        int farthestIdx = -1;
                        int farthestUse = -1;
                        for (int i = 0; i < numFrames; i++) {
                            int framePage = frames[i];
                            int nextUse = Integer.MAX_VALUE;
                            for (int k = p + 1; k < numPages; k++) {
                                if (pages[k] == framePage) {
                                    nextUse = k;
                                    break;
                                }
                            }
                            if (nextUse == Integer.MAX_VALUE) {
                                replaceIdx = i;
                                break;
                            } else {
                                if (nextUse > farthestUse) {
                                    farthestUse = nextUse;
                                    farthestIdx = i;
                                }
                            }
                        }
                        if (replaceIdx == -1) {
                            replaceIdx = farthestIdx;
                        }
                    }
                    frames[replaceIdx] = page;
                    faults++;
                }
                steps.add(new Step(page, hit, frames.clone()));
            }
        } else {
            System.out.println("{\"error\": \"Invalid algorithm type\"}");
            return;
        }

        // Print output in JSON format
        System.out.println("{");
        System.out.println("  \"totalFaults\": " + faults + ",");
        System.out.println("  \"steps\": [");
        for (int i = 0; i < steps.size(); i++) {
            Step s = steps.get(i);
            System.out.println("    {");
            System.out.println("      \"page\": " + s.page + ",");
            System.out.println("      \"hit\": " + s.hit + ",");
            System.out.print("      \"frames\": [");
            for (int f = 0; f < numFrames; f++) {
                System.out.print(s.frames[f] == -1 ? "null" : s.frames[f]);
                if (f < numFrames - 1) System.out.print(", ");
            }
            System.out.println("]");
            System.out.print("    }");
            if (i < steps.size() - 1) System.out.println(",");
            else System.out.println();
        }
        System.out.println("  ]");
        System.out.println("}");
    }

    static class Step {
        int page;
        boolean hit;
        int[] frames;
        public Step(int page, boolean hit, int[] frames) {
            this.page = page;
            this.hit = hit;
            this.frames = frames;
        }
    }
}
