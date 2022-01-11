/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pspace.ifs.KSAN.ObjManger;

import java.sql.SQLException;
import java.util.List;
import java.util.SortedMap;

import com.pspace.ifs.KSAN.ObjManger.ObjManagerException.ResourceAlreadyExistException;
import com.pspace.ifs.KSAN.ObjManger.ObjManagerException.ResourceNotFoundException;
import com.pspace.ifs.KSAN.s3gw.exception.S3Exception;
import com.pspace.ifs.KSAN.s3gw.identity.ObjectListParameter;
import com.pspace.ifs.KSAN.s3gw.multipart.Multipart;
import com.pspace.ifs.KSAN.s3gw.multipart.Part;
import com.pspace.ifs.KSAN.s3gw.multipart.ResultParts;
import com.pspace.ifs.KSAN.s3gw.multipart.ResultUploads;

/**
 *
 * @author legesse
 */
public interface DataRepository {
    public int insertObject(Metadata md) throws ResourceNotFoundException;
    public int updateDisks(Metadata md);
    public int updateSizeTime(Metadata md);
    public Metadata selectSingleObject(String diskPoolId, String bucketName, String path) throws ResourceNotFoundException;
    public Metadata selectSingleObject(String diskPoolId, String bucketName, String path, String versionId) throws ResourceNotFoundException;
    public Metadata selectSingleObjectWithObjId(String diskPoolId, String bucketName, String objid) throws ResourceNotFoundException; 
    //public ObjectListParameter selectObjects(String bucketName, Object query, int maxKeys) throws SQLException;
    public void selectObjects(String bucketName, Object query, int maxKeys, DBCallBack callback) throws SQLException;
    public int deleteObject(String bucketName, String path, String versionId);
    public List<String> getAllUsedDiskId() throws SQLException;
    public Bucket insertBucket(String bucketName, String diskPoolId, String userId, String acl, int replicaCount)throws ResourceAlreadyExistException;
    public int deleteBucket(String bucketName);
    public Bucket selectBucket(String bucketName)throws ResourceNotFoundException, SQLException;
    public void loadBucketList(); 
    public List<Bucket> getBucketList();
    public int updateBucketVersioning(Bucket bt);
    
    public int insertMultipartUpload(String bucket, String objkey, String uploadid, int partNo, String acl, String meta, String etag, long size) throws SQLException;
    public int updateMultipartUpload(String bucket, String uploadid, int partNo, boolean iscompleted) throws SQLException;
    public int deleteMultipartUpload(String bucket,  String uploadid) throws SQLException;
    public List<Integer> selectMultipart(String bucket, String uploadid, int maxParts, int partNoMarker) throws SQLException;
    public void selectMultipartUpload(String bucketName, Object query, int maxKeys, DBCallBack callback) throws SQLException;

    public Multipart getMulipartUpload(String uploadid) throws SQLException;
    public SortedMap<Integer, Part> getParts(String uploadId) throws SQLException;
    public ResultParts getParts(String uploadId, String partNumberMarker, int maxParts) throws SQLException;
    public ResultUploads getUploads(String bucket, String delimiter, String prefix, String keyMarker, String uploadIdMarker, int maxUploads) throws SQLException, S3Exception;
    public void updateObjectMeta(String bucket, String objkey, String versionid, String meta) throws SQLException;
    public boolean isUploadId(String uploadid) throws SQLException;
    
    public void updateBucketAcl(Bucket bt) throws SQLException;
    public void updateBucketCors(Bucket bt) throws SQLException;
    public void updateBucketWeb(Bucket bt) throws SQLException;
    public void updateBucketLifecycle(Bucket bt) throws SQLException;
    public void updateBucketAccess(Bucket bt) throws SQLException;
    public void updateBucketTagging(Bucket bt) throws SQLException;
    public void updateBucketReplication(Bucket bt) throws SQLException;
    public boolean isBucketDeleted(String bucket) throws SQLException;

    //public ObjectListParameter listObject(String bucketName, String delimiter, String marker, int maxKeys, String prefix) throws SQLException;
    //public ObjectListParameter listObjectV2(String bucketName, String delimiter, String startAfter, String continueationToken, int maxKeys, String prefix) throws SQLException;
    //public ObjectListParameter listObjectVersions(String bucketName, String delimiter, String keyMarker, String versionIdMarker, int maxKeys, String prefix) throws SQLException;
    
    public List<Object> utilJobMgt(String operation, List<Object> in);
    
    public int insertUserDiskPool(String userId, String accessKey, String secretKey, String diskpoolId, int replicaCount) throws SQLException;
    public Bucket getUserDiskPool(Bucket bt) throws SQLException;
    public int deleteUserDiskPool(String userId, String diskPoolId) throws SQLException;
    
    public Object getStatement(String query) throws SQLException;
    public List<Metadata> getObjectList(String bucketName, Object pstmt, int maxKeys) throws SQLException;
}
