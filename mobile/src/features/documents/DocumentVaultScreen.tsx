import React from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';

const documents = [
  { id: '1', type: 'Photograph', status: 'VERIFIED' },
  { id: '2', type: 'Signature', status: 'VERIFIED' },
  { id: '3', type: '10th Marksheet', status: 'PENDING' },
  { id: '4', type: '12th Marksheet', status: 'MISSING' },
];

const DocumentVaultScreen = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Document Vault</Text>
      <FlatList
        data={documents}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View>
              <Text style={styles.docType}>{item.type}</Text>
              <Text style={[styles.status, { color: getStatusColor(item.status) }]}>{item.status}</Text>
            </View>
            <TouchableOpacity style={styles.uploadBtn}>
              <Text style={styles.uploadBtnText}>{item.status === 'MISSING' ? 'Upload' : 'Replace'}</Text>
            </TouchableOpacity>
          </View>
        )}
      />
    </View>
  );
};

const getStatusColor = (status: string) => {
  switch (status) {
    case 'VERIFIED': return '#4caf50';
    case 'PENDING': return '#ff9800';
    case 'MISSING': return '#f44336';
    default: return '#000';
  }
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16, backgroundColor: '#f5f5f5' },
  title: { fontSize: 24, fontWeight: 'bold', marginBottom: 20 },
  card: { backgroundColor: '#fff', padding: 16, borderRadius: 8, flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 },
  docType: { fontSize: 16, fontWeight: 'bold' },
  status: { fontSize: 12, marginTop: 4 },
  uploadBtn: { backgroundColor: '#2196f3', paddingVertical: 6, paddingHorizontal: 12, borderRadius: 4 },
  uploadBtnText: { color: '#fff', fontSize: 12, fontWeight: 'bold' }
});

export default DocumentVaultScreen;
