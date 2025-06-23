package com.omnilinx.file_upload_rest_svc.helper;


import java.util.List;

public interface FileParser<T> {

    List<T> parse(byte[] content);
}
