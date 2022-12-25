package com.zbro.messydoc.worker.file;

import java.util.Set;

public class FileSetInspector {

    /**
     *
     * @param oldSet A = 1 2 3 4  5
     * @param newSet B = 1   3 4' 5 6
     * @return B - A = 4', 6 ,the files need to be updated
     */
    public Set<FileRawEntity> getDifferentInNewSet(Set<FileRawEntity> oldSet, Set<FileRawEntity> newSet){
        newSet.removeAll(oldSet);
        return newSet;
    }

    /**
     *
     * @param oldSet A = 1 2 3 4  5
     * @param newSet B = 1   3 4' 5 6
     * @return A - B = 2, 4 , the files no longer exist
     */
    public  Set<FileRawEntity> getDifferentInOldSet(Set<FileRawEntity> oldSet, Set<FileRawEntity> newSet){
        oldSet.removeAll(newSet);
        return newSet;
    }

    /**
     *
     * @param oldSet
     * @param newSet
     * @return Intersection of the sets ,the files stay unmodified
     */
    public  Set<FileRawEntity> getIntersection(Set<FileRawEntity> oldSet, Set<FileRawEntity> newSet){
        Set<FileRawEntity> set1 = getDifferentInNewSet(oldSet,newSet);
        Set<FileRawEntity> set2 = getDifferentInOldSet(oldSet,newSet);
        oldSet.addAll(newSet);
        oldSet.removeAll(set1);
        oldSet.removeAll(set2);
        return oldSet;
    }

}
